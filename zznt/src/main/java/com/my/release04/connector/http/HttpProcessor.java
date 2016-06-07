package com.my.release04.connector.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.catalina.Globals;
import com.catalina.Lifecycle;
import com.catalina.LifecycleException;
import com.catalina.LifecycleListener;
import com.util.RequestUtil;
import com.util.ServerInfo;
import com.util.StringManager;
import com.util.StringParser;

/**
 * Implementation of a request processor (and its associated thread) that may
 * be used by an HttpConnector to process individual requests.  The connector
 * will allocate a processor from its pool, assign a particular socket to it,
 * and the processor will then execute the processing required to complete
 * the request.  When the processor is completed, it will recycle itself.
 */
public class HttpProcessor implements Runnable, Lifecycle{
	
	private static final String SERVER_INFO = 
			ServerInfo.getServerInfo() + " (HTTP/1.1 Connector)";
			
	public HttpProcessor(HttpConnector connector, int id) {

        super();
        this.connector = connector;
        this.debug = connector.getDebug();
        this.id = id;
        this.proxyName = connector.getProxyName();
        this.proxyPort = connector.getProxyPort();
        this.request = (HttpRequestImpl) connector.createRequest();
        this.response = (HttpResponseImpl) connector.createResponse();
        this.serverPort = connector.getPort();
        this.threadName =
          "HttpProcessor[" + connector.getPort() + "][" + id + "]";

    }
	
	private boolean available = false;
	private HttpConnector connector = null;
	private int debug = 0;
	private int id = 0;
	
	private static final String match = 
			";" + Globals.SESSION_PARAMETER_NAME + "=";
	private static final char[] SESSION_ID = match.toCharArray();
	private StringParser parser = new StringParser();
	
	
	private String proxyName = null;
	private int proxyPort = 0;
	private HttpRequestImpl request;
	private HttpResponseImpl response;
	private int serverPort = 0;
	
	private Socket socket = null;
	
	private boolean started = false;
	private boolean stopped = false;
	
	private Thread thread = null;
	private String threadName = null;
	/**
     * The thread synchronization object.
     */
    private Object threadSync = new Object();
    
    protected StringManager sm =
            StringManager.getManager(Constants.Package);
	
    /**
     * Process an incoming TCP/IP connection on the specified socket.  Any
     * exception that occurs during processing must be logged and swallowed.
     * <b>NOTE</b>:  This method is called from our Connector's thread.  We
     * must assign it to our own thread so that multiple simultaneous
     * requests can be handled.
     *
     * @param socket TCP socket to process
     */
	synchronized void assign(Socket socket) {
		
		// Wait for the Processor to get the previous Socket
		while (available) {
			try {
				wait();
			} catch (InterruptedException e) {}
			
		}
			
		// Store the newly available Socket and notify our thread
		this.socket = socket;
		available = true;
		notifyAll();
		
		if ((debug >= 1) && (socket != null))
			log(" An incoming request is being assigned");
	}
	
	
	private synchronized Socket await() {
		
		// Wait for the Connector to provide a new Socket
		while (!available) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		
		// notify the Connector that we have received this socket
		Socket socket = this.socket;
		available = false;
		notifyAll();
		
		if ((debug >= 1) && (socket != null))
            log("  The incoming request has been awaited");
		
		return (socket);
	}
	
	
	private void process(Socket socket2) {
		boolean ok = true;
        InputStream input = null;
        OutputStream output = null;

        // Construct and initialize the objects we will need
        try {
            input = new BufferedInputStream(socket.getInputStream(),
                                            connector.getBufferSize());
            request.setStream(input);
            request.setResponse(response);
            output = socket.getOutputStream();
            response.setStream(output);
            response.setRequest(request);
            ((HttpServletResponse) response.getResponse()).setHeader
                ("Server", SERVER_INFO);
        } catch (Exception e) {
            log("process.create", e);
            ok = false;
        }

        // Parse the incoming request
        try {
            if (ok) {
                parseConnection(socket);
                parseRequest(input);
                if (!request.getRequest().getProtocol().startsWith("HTTP/0"))
                    parseHeaders(input);
            }
        } catch (Exception e) {
            try {
                log("process.parse", e);
                ((HttpServletResponse) response.getResponse()).sendError
                    (HttpServletResponse.SC_BAD_REQUEST);
            } catch (Exception f) {
                ;
            }
        }

        // Ask our Container to process this request
        try {
            if (ok) {
                connector.getContainer().invoke(request, response);
            }
        } catch (ServletException e) {
            log("process.invoke", e);
            try {
                ((HttpServletResponse) response.getResponse()).sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception f) {
                ;
            }
            ok = false;
        } catch (Throwable e) {
            log("process.invoke", e);
            try {
                ((HttpServletResponse) response.getResponse()).sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception f) {
                ;
            }
            ok = false;
        }

        // Finish up the handling of the response
        try {
            if (ok)
                response.finishResponse();
        } catch (IOException e) {
            log("FIXME-Exception from finishResponse", e);
        }
        try {
            if (output != null)
                output.flush();
        } catch (IOException e) {
            log("FIXME-Exception flushing output", e);
        }
        try {
            if (output != null)
                output.close();
        } catch (IOException e) {
            log("FIXME-Exception closing output", e);
        }

        // Finish up the handling of the request
        try {
            if (ok)
                request.finishRequest();
        } catch (IOException e) {
            log("FIXME-Exception from finishRequest", e);
        }
        try {
            if (input != null)
                input.close();
        } catch (IOException e) {
            log("FIXME-Exception closing input", e);
        }

        // Finish up the handling of the socket connection itself
        try {
            socket.close();
        } catch (IOException e) {
            log("FIXME-Exception closing socket", e);
        }
        socket = null;
		
	}
	// ---------------------------------------------- Background Thread Methods
	
	private void parseHeaders(InputStream input)
	        throws IOException, ServletException {

        while (true) {

            // Read the next header line
            String line = read(input);
            if ((line == null) || (line.length() < 1))
                break;

            // Parse the header name and value
            int colon = line.indexOf(':');
            if (colon < 0)
                throw new ServletException
                    (sm.getString("httpProcessor.parseHeaders.colon"));
            String name = line.substring(0, colon).trim();
            String match = name.toLowerCase();
            String value = line.substring(colon + 1).trim();
            if (debug >= 1)
                log(" Header " + name + " = " + value);

            // Set the corresponding request headers
            if (match.equals("authorization")) {
                request.setAuthorization(value);
                request.addHeader(name, value);
            } else if (match.equals("accept-language")) {
          request.addHeader(name, value);
          //
          // Adapted from old code perhaps maybe optimized
          //
          //
          Hashtable languages = new Hashtable();
          StringTokenizer languageTokenizer = new StringTokenizer(value, ",");

          while (languageTokenizer.hasMoreTokens()) {
            String language = languageTokenizer.nextToken().trim();
            int qValueIndex = language.indexOf(';');
            int qIndex = language.indexOf('q');
            int equalIndex = language.indexOf('=');
            Double qValue = new Double(1);

            if (qValueIndex > -1 && qValueIndex < qIndex && qIndex < equalIndex) {
              String qValueStr = language.substring(qValueIndex + 1);
              language = language.substring(0, qValueIndex);
              qValueStr = qValueStr.trim().toLowerCase();
              qValueIndex = qValueStr.indexOf('=');
              qValue = new Double(0);
              if (qValueStr.startsWith("q") &&
                  qValueIndex > -1) {
                  qValueStr = qValueStr.substring(qValueIndex + 1);
                  try {
                      qValue = new Double(qValueStr.trim());
                  } catch (NumberFormatException nfe) {
                  }
              }
            }
            // XXX
            // may need to handle "*" at some point in time
            if (! language.equals("*")) {
                String key = qValue.toString();
                Vector v = (Vector)((languages.containsKey(key)) ? languages.get(key) : new Vector());
                v.addElement(language);
                languages.put(key, v);
            }
          }
          Vector l = new Vector();
          Enumeration e = languages.keys();
          while (e.hasMoreElements()) {
              String key = (String)e.nextElement();
              Vector v = (Vector)languages.get(key);
              Enumeration le = v.elements();
              while (le.hasMoreElements()) {
                String language = (String)le.nextElement();
                String country = "";
                String variant = "";
                int countryIndex = language.indexOf('-');
                if (countryIndex > -1) {
                    country = language.substring(countryIndex + 1).trim();
                    language = language.substring(0, countryIndex).trim();
                    int vDash = country.indexOf("-");
                    if (vDash > 0) {
                        String cTemp = country.substring(0, vDash);
                        variant = country.substring(vDash + 1);
                        country = cTemp;
                    } 
                }
                request.addLocale(new Locale(language, country, variant));
              }
          }
            } else if (match.equals("cookie")) {
                Cookie cookies[] = RequestUtil.parseCookieHeader(value);
                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getName().equals
                        (Globals.SESSION_COOKIE_NAME)) {
                        // Override anything requested in the URL
                        if (!request.isRequestedSessionIdFromCookie()) {
                            // Accept only the first session id cookie
                            request.setRequestedSessionId
                                (cookies[i].getValue());
                            request.setRequestedSessionCookie(true);
                            request.setRequestedSessionURL(false);
                            if (debug >= 1)
                                log(" Requested cookie session id is " +
                                    ((HttpServletRequest) request.getRequest())
                                    .getRequestedSessionId());
                        }
                    }
                    request.addCookie(cookies[i]);
                }
                // Keep Watchdog from whining by adding the header as well
                // (GetHeaderTest, GetIntHeader_1Test)
                request.addHeader(name, value);
            } else if (match.equals("content-length")) {
                int n = -1;
                try {
                    n = Integer.parseInt(value);
                } catch (Exception e) {
                    throw new ServletException
                        (sm.getString("httpProcessor.parseHeaders.contentLength"));
                }
                request.setContentLength(n);
                request.addHeader(name, value);
            } else if (match.equals("content-type")) {
                request.setContentType(value);
                request.addHeader(name, value);
            } else if (match.equals("host")) {
                int n = value.indexOf(':');
                if (n < 0)
                    request.setServerName(value);
                else {
                    request.setServerName(value.substring(0, n).trim());
                    int port = 80;
                    try {
                        port = Integer.parseInt(value.substring(n+1).trim());
                    } catch (Exception e) {
                        throw new ServletException
                            (sm.getString("httpProcessor.parseHeaders.portNumber"));
                    }
                    request.setServerPort(port);
                }
                request.addHeader(name, value);
            } else {
                request.addHeader(name, value);
            }
        }

    }


	private void parseRequest(InputStream input) throws IOException, ServletException {
		// Parse the incoming request line
        String line = read(input);
        if (line == null)
            throw new ServletException
                (sm.getString("httpProcessor.parseRequest.read"));
        StringTokenizer st = new StringTokenizer(line);

        String method = null;
        try {
            method = st.nextToken();
        } catch (NoSuchElementException e) {
            method = null;
        }

        String uri = null;
        try {
            uri = st.nextToken();
            ;   // FIXME - URL decode the URI?
        } catch (NoSuchElementException e) {
            uri = null;
        }

        String protocol = null;
        try {
            protocol = st.nextToken();
        } catch (NoSuchElementException e) {
            protocol = "HTTP/0.9";
        }

        // Validate the incoming request line
        if (method == null) {
            throw new ServletException
                (sm.getString("httpProcessor.parseRequest.method"));
        } else if (uri == null) {
            throw new ServletException
                (sm.getString("httpProcessor.parseRequest.uri"));
        }

        // Parse any query parameters out of the request URI
        int question = uri.indexOf('?');
        if (question >= 0) {
            request.setQueryString(uri.substring(question + 1));
            if (debug >= 1)
                log(" Query string is " +
                    ((HttpServletRequest) request.getRequest()).getQueryString());
            uri = uri.substring(0, question);
        } else
            request.setQueryString(null);

        // Parse any requested session ID out of the request URI
        int semicolon = uri.indexOf(match);
        if (semicolon >= 0) {
            String rest = uri.substring(semicolon + match.length());
            int semicolon2 = rest.indexOf(';');
            if (semicolon2 >= 0) {
                request.setRequestedSessionId(rest.substring(0, semicolon2));
                rest = rest.substring(semicolon2);
            } else {
                request.setRequestedSessionId(rest);
                rest = "";
            }
            request.setRequestedSessionURL(true);
            uri = uri.substring(0, semicolon) + rest;
            if (debug >= 1)
                log(" Requested URL session id is " +
                    ((HttpServletRequest) request.getRequest()).getRequestedSessionId());
        } else {
            request.setRequestedSessionId(null);
            request.setRequestedSessionURL(false);
        }

        // Set the corresponding request properties
        ((HttpRequest) request).setMethod(method);
        request.setProtocol(protocol);
        ((HttpRequest) request).setRequestURI(uri);
        request.setSecure(false);       // No SSL support
        request.setScheme("http");      // No SSL support

        if (debug >= 1)
            log(" Request is " + method + " for " + uri);
		
	}


	private void parseConnection(Socket socket)
        throws IOException, ServletException {

        if (debug >= 2)
            log("  parseConnection: address=" + socket.getInetAddress() +
                ", port=" + connector.getPort());
        ((HttpRequestImpl) request).setInet(socket.getInetAddress());
        if (proxyPort != 0)
            request.setServerPort(proxyPort);
        else
            request.setServerPort(serverPort);
        request.setSocket(socket);
    }

	private String read(InputStream input) throws IOException {

        StringBuffer sb = new StringBuffer();
        while (true) {
            int ch = input.read();
            if (ch < 0) {
                if (sb.length() == 0) {
                    return (null);
                } else {
                    break;
                }
            } else if (ch == '\r') {
                continue;
            } else if (ch == '\n') {
                break;
            }
            sb.append((char) ch);
        }
        if (debug >= 2)
            log("  Read: " + sb.toString());
        return (sb.toString());

    }
	
	
	// --------------------------------- thread methods 

	public void run() {
		while (!stopped) {
			
			Socket socket = await();
			if (socket == null)
				continue;
			
			process(socket);
			
			// Finish up this request
            request.recycle();
            response.recycle();
            connector.recycle(this);
		}
		
		
		synchronized (threadSync) {
			threadSync.notifyAll();
		}
	}
	
	private void threadStart() {

		thread = new Thread(this, threadName);
		thread.setDaemon(true);
		thread.start();
	}
	
	private void threadStop() {
		stopped = true;
		assign(null);
		synchronized (threadSync) {
			try {
				threadSync.wait(5000);
			} catch (InterruptedException e) {}
		}
		thread = null;
	}
	
	// ------------------------------------------------ lifeCycle methods
	
	public LifecycleListener[] findLifecycleListeners() {
		return null;
	}
	public void addLifecycleListener(LifecycleListener listener) {}
	public void removeLifecycleListener(LifecycleListener listener) {}
	public void start() throws LifecycleException {
		if (started)
            throw new LifecycleException
                (sm.getString("httpProcessor.alreadyStarted"));
        started = true;

        threadStart();
		
	}
	public void stop() throws LifecycleException {
		if (!started)
            throw new LifecycleException
                (sm.getString("httpProcessor.notStarted"));
        started = false;

        threadStop();
	}
	
	
	// loging
	private void log(String string) {
		System.out.println(string);
	}
	
	private void log(String string, Throwable e) {
		log(string);
	}
}
