package com.my.release03.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import com.my.release03.ServletProcessor;
import com.my.release03.StaticResourceProcessor;
import com.my.release03.StringManager;
import com.util.RequestUtil;

public class HttpProcessor {
	protected StringManager sm = StringManager.getManager("com.my.release03.connector.http");
	
	@SuppressWarnings("unused")
	private HttpConnector connector = null;
	
	private HttpRequest request;
	private HttpRequestLine requestLine = new HttpRequestLine();
	private HttpResponse response;
	
	
	
	public HttpProcessor(HttpConnector connector) {
		this.connector = connector; 
	}

	public void process(Socket socket) {
		SocketInputStream input = null;
		OutputStream output = null;
		try {
			input = new SocketInputStream(socket.getInputStream(), 2048);
			output = socket.getOutputStream();
			
			// create HttpRequest object and parse
			request = new HttpRequest(input);
			
			// create HttpResponse object
			response = new HttpResponse(output);
			response.setRequest(request);
			response.setHeader("server", "Pyrmont Servlet Container");
			
			parseRequest(input, output);
			parseHeaders(input);
			
			//check if this is a request for a servlet or a static resource 
			//a request for a servlet begins with "/servlet/" 
			if (request.getRequestURI().startsWith("/servlet/")) { 
				ServletProcessor processor = new ServletProcessor(); 
				processor.process(request, response); 
			} else {
				StaticResourceProcessor processor = new StaticResourceProcessor();
				processor.process(request, response);
			}
			
			// close the socket
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseHeaders(SocketInputStream input) throws ServletException, IOException {
		while (true) {
			HttpHeader header = new HttpHeader(); 
			// Read the next header 
			input.readHeader(header);
			
			if (header.nameEnd == 0) {
				if (header.valueEnd == 0) {
					return;
				} else {
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.colon"));
				}		
			}
			String name = new String(header.name, 0, header.nameEnd);
			String value = new String(header.value, 0, header.valueEnd);
			request.addHeader(name, value);
		
			// do something for some headers, ignore others.
			if (name.equals("cookie")) {
				Cookie cookies[] = RequestUtil.parseCookieHeader(value);
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals("jsessionid")) {
						// Override anything requested in the URL
			            if (!request.isRequestedSessionIdFromCookie()) {
			            	// Accept only the first session id cookie
			            	request.setRequestedSessionId(cookies[i].getValue());
			            	request.setRequestedSessionCookie(true);
			            	request.setRequestedSessionURL(false);
			            }
					}
					request.addCookie(cookies[i]);
				}
			} else if (name.equals("content-length")) {
				int n = -1;
				try {
					n = Integer.parseInt(value);
				} catch (Exception e) {
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
				}
				request.setContentLength(n);
			} else if (name.equals("content-type")) {
				request.setContentType(value); 
			}
		}
	}

	private void parseRequest(SocketInputStream input, OutputStream output) 
		throws ServletException, IOException {
		// parse the incoming request line
		input.readRequestLine(requestLine);
		
		String method = 
			new String(requestLine.method, 0, requestLine.methodEnd);
		String uri = null;
		String protocol = 
			new String(requestLine.protocol, 0, requestLine.protocolEnd);
		
		// Vaildate incoming request line
		if (method.length () < 1) {
			throw new ServletException("Missing HTTP request method"); 
		} else if (requestLine.uriEnd < 1) { 
			throw new ServletException("Missing HTTP request URI"); 
		}
		
		// Parse any query parameters out of the request URI
		int question = requestLine.indexOf("?");
		if (question >= 0) {	// there is a query string.
			request.setQueryString(
				new String(requestLine.uri, question + 1, requestLine.uriEnd - question - 1));
			uri = new String(requestLine.uri, 0, question);
		} else {
			request.setQueryString(null);
			uri = new String(requestLine.uri, 0, requestLine.uriEnd);
		}
		
		// Checking for an absolute URI (with the HTTP protocol)
		if (!uri.startsWith("/")) {
			// not starting with /, this is an absolute URI
			int pos = uri.indexOf("://");
			// Parsing out protocol and host name
			if (pos != -1) {
				pos = uri.indexOf('/', pos + 3);
				if (pos == -1) {
					uri = "";
				} else {
					uri = uri.substring(pos);
				}
			}
		}
		
		// parse any requested session ID of out of request URI
		String match = ";jseesionId=";
		int semicolon = uri.indexOf(match);
		if (semicolon >= 0) {
			String rest = uri.substring(semicolon + match.length());
			int semicolon2 = rest.indexOf(';');
			if (semicolon2 >= 0) {
				request.setRequestSessionId(rest.substring(0, semicolon2));
				rest = rest.substring(semicolon2);
			} else {
				request.setRequestSessionId(rest);
				rest = "";
			}
			request.setRequestedSessionURL(true);
			uri = uri.substring(0, semicolon) + rest;
		} else {
			request.setRequestSessionId(null);
			request.setRequestedSessionURL(false);
		}
		
		// Normalize URI (using String operations at the moment)
	    String normalizedUri = normalize(uri);
	    
	    // Set the corresponding request properties
	    ((HttpRequest) request).setMethod(method);
	    request.setProtocol(protocol);
	    if (normalizedUri != null) {
	    	((HttpRequest) request).setRequestURI(normalizedUri);
	    } else {
	    	((HttpRequest) request).setRequestURI(uri);
	    }
	    
	    if (normalizedUri == null) {
	    	throw new ServletException("Invaild URI: '" + uri + "'");
	    }
	}

	
	/**
	 * 
	 * Return a context-relative path, beginning with a "/", that represents
	 * the canonical version of the specified path after ".." and "." elements
	 * are resolved out.  If the specified path attempts to go outside the
	 * boundaries of the current context (i.e. too many ".." path elements
	 * are present), return <code>null</code> instead.
   	 *
   	 * @param path Path to be normalized
   	 */
	protected String normalize(String path) {
		if (path == null)
			return null;
		// Create a place for the normalized path
	    String normalized = path;
		
		// Normalize "/%7E" and "/%7e" at the beginning to "/~"
	    if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
	      normalized = "/~" + normalized.substring(4);
	    
	    // Prevent encoding '%', '/', '.' and '\', which are special reserved
	    // characters
	    if ((normalized.indexOf("%25") >= 0)
	      || (normalized.indexOf("%2F") >= 0)
	      || (normalized.indexOf("%2E") >= 0)
	      || (normalized.indexOf("%5C") >= 0)
	      || (normalized.indexOf("%2f") >= 0)
	      || (normalized.indexOf("%2e") >= 0)
	      || (normalized.indexOf("%5c") >= 0)) {
	      return null;
	    }
	    
	    if (normalized.equals("/."))
	        return "/";
	    
	    // Normalize the slashes and add leading slash if necessary
	    if (normalized.indexOf('\\') >= 0)
	      normalized = normalized.replace('\\', '/');
	    if (!normalized.startsWith("/"))
	      normalized = "/" + normalized;

	    // Resolve occurrences of "//" in the normalized path
	    while (true) {
	      int index = normalized.indexOf("//");
	      if (index < 0)
	        break;
	      normalized = normalized.substring(0, index) +
	        normalized.substring(index + 1);
	    }

	    // Resolve occurrences of "/./" in the normalized path
	    while (true) {
	      int index = normalized.indexOf("/./");
	      if (index < 0)
	        break;
	      normalized = normalized.substring(0, index) +
	        normalized.substring(index + 2);
	    }

	    // Resolve occurrences of "/../" in the normalized path
	    while (true) {
	      int index = normalized.indexOf("/../");
	      if (index < 0)
	        break;
	      if (index == 0)
	        return (null);  // Trying to go outside our context
	      int index2 = normalized.lastIndexOf('/', index - 1);
	      normalized = normalized.substring(0, index2) +
	        normalized.substring(index + 3);
	    }

	    // Declare occurrences of "/..." (three or more dots) to be invalid
	    // (on some Windows platforms this walks the directory tree!!!)
	    if (normalized.indexOf("/...") >= 0)
	      return (null);

	    // Return the normalized path that we have completed
	    return (normalized);
	}
}
