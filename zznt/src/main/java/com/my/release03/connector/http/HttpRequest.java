package com.my.release03.connector.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.my.release03.connector.RequestStream;
import com.util.Enumerator;
import com.util.ParameterMap;
import com.util.RequestUtil;

public class HttpRequest implements HttpServletRequest{
	
	private String contentType;
	private int contentLength;
	private InetAddress inetAddress;
	private InputStream input;
	private String method;
	private String protocol;
	private String queryString;
	private String requestURI;
	private String serverName;
	private int serverPort;
	private Socket socket;
	private boolean requestedSessionCookie;
	private String requestedSessionId;
	private boolean requestedSessionURL;
	
	
	/**
	 * the request attributes for request
	 */
	protected HashMap attributes = new HashMap();
	/**
	 * The authorization credentials sent with this Request.
	 */
	protected String authorization = null;
	/**
	 * The context path for this request.
	 */
	protected String contextPath = "";
	/**
	 * The set of cookies associated with this Request.
	 */
	protected ArrayList cookies = new ArrayList();
	/**
	 * An empty collection to use for returning empty Enumerations.  Do not
	 * add any elements to this collection!
	 */
	protected static ArrayList empty = new ArrayList();
	/**
	 * The set of SimpleDateFormat formats to use in getDateHeader().
	 */
	protected SimpleDateFormat formats[] = {
		new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
		new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
		new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)
	};
	
	/**
	 * The HTTP headers associated with this Request, keyed by name.  The
	 * values are ArrayLists of the corresponding header values.
	 */
	protected HashMap headers = new HashMap();
	
	/**
	  * The parsed parameters for this request.  This is populated only if
	  * parameter information is requested via one of the
	  * <code>getParameter()</code> family of method calls.  The key is the
	  * parameter name, while the value is a String array of values for this
	  * parameter.
	  * <p>
	  * <strong>IMPLEMENTATION NOTE</strong> - Once the parameters for a
	  * particular request are parsed and stored here, they are not modified.
	  * Therefore, application level access to the parameters need not be
	  * synchronized.
	 */
	protected ParameterMap parameters = null;
	/**
	  * Have the parameters for this request been parsed yet?
	  */
	protected boolean parsed = false;
	protected String pathInfo = null;
	
	/**
	  * The reader that has been returned by <code>getReader</code>, if any.
	  */
	protected BufferedReader reader = null;

	/**
	  * The ServletInputStream that has been returned by
	  * <code>getInputStream()</code>, if any.
	 */
	protected ServletInputStream stream = null;
	
	public HttpRequest(SocketInputStream input) {
		this.input = input;
	}

	public void addHeader(String name, String value) {
		name = name.toLowerCase();
		synchronized (headers) {
			ArrayList values = (ArrayList) headers.get(name);
			if (values == null) {
				values = new ArrayList();
				headers.put(name, values);
			}
			values.add(value);
		}
	}
	/**
	 * Parse the parameters of this request, if it has not already occurred.
	 * If parameters are present in both the query string and the request
	 * content, they are merged.
	 */
	protected void parseParameters() {
		if (parsed)
			return;
		
		ParameterMap results = parameters;
		if (results == null)
			results = new ParameterMap();
		results.setLocked(false);
		String encoding = getCharacterEncoding();
		if (encoding == null)
			encoding = "ISO-8859-1";
		
		// Parse any parameters specified in the query string
		String queryString = getQueryString();
		try {
		    RequestUtil.parseParameters(results, queryString, encoding);
	    }
	    catch (UnsupportedEncodingException e) {}
		
		// parse any parameters specified in the input stream
		String contentType = getContentType();
		if (contentType == null)
			contentType = "";
		int semicolon = contentType.indexOf(';');
		if (semicolon >= 0) {
			contentType = contentType.substring(0, semicolon).trim();
		} else {
			contentType = contentType.trim();
		}
		if ("POST".equals(getMethod()) && (getContentLength() > 0) 
			&& "application/x-www-form-urlencoded".equals(contentType)) {
			try {
				int max = getContentLength();
				int len = 0;
				byte buf[] = new byte[getContentLength()];
				ServletInputStream is = getInputStream();
				while (len < max) {
					int next = is.read(buf, len, max - len);
					if (next < 0) {
						break;
					}
					len += next;
				}
				is.close();
				if (len < max) {
					throw new RuntimeException("Content length mismatch");
				}
				RequestUtil.parseParameters(results, buf, encoding);
			} catch (UnsupportedEncodingException ue) {
				;
			} catch (IOException e) {
				 throw new RuntimeException("Content read fail");
			}
		}
		
		// Store the final results
		results.setLocked(true);
		parsed = true;
		parameters = results;
	}
	
	
	/**
	 * Create and return a ServletInputStream to read the content
	 * associated with this Request.  The default implementation creates an
	 * instance of RequestStream associated with this request, but this can
	 * be overridden if necessary.
	 *
	 * @exception IOException if an input/output error occurs
	 */
	public ServletInputStream createInputStream() throws IOException {
		return (new RequestStream(this));
	}
	
	public InputStream getStream() {
	    return input;
	}
	
	public int getContentLength() {
		return contentLength;
	}
	
	public ServletInputStream getInputStream() throws IOException {
		if (reader != null)
			throw new IllegalStateException("getInputStream has been called");

	    if (stream == null)
	    	stream = createInputStream();
	    return (stream);
	}

	public void setContentLength(int length) {
		this.contentLength = length;
	}
	
	public void setContentType(String value) {
		this.contentType = value;
	}
	
	public void setInet(InetAddress inetAddress) {
	    this.inetAddress = inetAddress;
	}
	
	public void setMethod(String method) {
	    this.method = method;
	}
	
	public String getContextPath() {
		return contextPath;
	}
	
	public String getMethod() {
		return method;
	}

	public String getContentType() {
		return this.contentType;
	}

	public String getQueryString() {
		return this.queryString;
	}

	public String getRequestURI() {
		return requestURI;
	}
	
	public void setPathInfo(String path) {
	    this.pathInfo = path;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getCharacterEncoding() {
		return null;
	}
	
	public void setQueryString(String string) {
		this.queryString = string;
	}

	public void setRequestURI(String requestURI) {
	    this.requestURI = requestURI;
	}
	
	/**
	  * Set the name of the server (virtual host) to process this request.
	  *
	  * @param name The server name
	  */
	public void setServerName(String name) {
	    this.serverName = name;
	}

	/**
	  * Set the port number of the server to process this request.
	  *
	  * @param port The server port
	  */
	public void setServerPort(int port) {
		this.serverPort = port;
	}
	
	public void setSocket(Socket socket) {
	    this.socket = socket;
	}
	
	public void setRequestedSessionCookie(boolean flag) {
		this.requestedSessionCookie = flag;
	}
	
	public void setRequestSessionId(String requestedSessionId) {
		this.requestedSessionId = requestedSessionId;
	}

	public void setRequestedSessionURL(boolean flag) {
		this.requestedSessionURL = flag;
	}

	/* implementation of the HttpServletRequest*/
	public Object getAttribute(String name) {
		synchronized (attributes) {
			return attributes.get(name);
		}
	}
	
	public Enumeration<String> getAttributeNames() {
		synchronized (attributes) {
		    return (new Enumerator(attributes.keySet()));
	    }
	}
	
	public Cookie[] getCookies() {
		synchronized (cookies) {
			if (cookies.size() < 1)
				return null;
			Cookie results[] = new Cookie[cookies.size()];
			return ((Cookie[]) cookies.toArray(results));
		}
	}
	
	
	public long getDateHeader(String name) {
		String value = getHeader(name);
		if (value == null)
			return (-1L);
		
		// Work around a bug in SimpleDateFormat in pre-JDK1.2b4
	    // (Bug Parade bug #4106807)
		value += " ";
		
		// Attempt to convert the date header in a variety of formats
	    for (int i = 0; i < formats.length; i++) {
	      try {
	    	  Date date = formats[i].parse(value);
	    	  return (date.getTime());
	      }
	      catch (ParseException e) {
	        ;
	      }
	    }
	    throw new IllegalArgumentException(value);
	}

	public String getHeader(String name) {
		name = name.toLowerCase();
		synchronized (headers) {
			ArrayList values = (ArrayList) headers.get(name);
			if (values != null)
		        return ((String) values.get(0));
		      else
		        return null;
		}
	}
	
	public Enumeration<String> getHeaderNames() {
		synchronized (headers) {
			return (new Enumerator(headers.keySet()));
	    }
	}
	
	
	public Enumeration<String> getHeaders(String name) {
		name = name.toLowerCase();
	    synchronized (headers) {
	    	ArrayList values = (ArrayList) headers.get(name);
	    	if (values != null)
	    		return (new Enumerator(values));
	    	else
	    		return (new Enumerator(empty));
	    }
	}
	
	public void setRequestedSessionId(String requestedSessionId) {
		this.requestedSessionId = requestedSessionId;
	}

	public int getIntHeader(String name) {
		String value = getHeader(name);
	    if (value == null)
	      return (-1);
	    else
	      return (Integer.parseInt(value));
	}
	
	public void addCookie(Cookie cookie) {
		// TODO Auto-generated method stub
		
	}
	
	public String getParameter(String name) {
		parseParameters();
	    String values[] = (String[]) parameters.get(name);
	    if (values != null)
	      return (values[0]);
	    else
	      return (null);
	}
	
	public Map<String, String[]> getParameterMap() {
		parseParameters();
	    return (this.parameters);
	}
	
	public Enumeration<String> getParameterNames() {
		parseParameters();
		return (new Enumerator(parameters.keySet()));
	}

	public String[] getParameterValues(String name) {
		parseParameters();
	    String values[] = (String[]) parameters.get(name);
	    if (values != null)
	      return (values);
	    else
	      return null;
	}
	
	public String getPathInfo() {
		return pathInfo;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public BufferedReader getReader() throws IOException {
		if (stream != null)
	      throw new IllegalStateException("getInputStream has been called.");
	    if (reader == null) {
	      String encoding = getCharacterEncoding();
	      if (encoding == null)
	        encoding = "ISO-8859-1";
	      InputStreamReader isr =
	        new InputStreamReader(createInputStream(), encoding);
	        reader = new BufferedReader(isr);
	    }
	    return (reader);
	}
	
	
	/**
	 * Set the authorization credentials sent with this request.
	 *
	 * @param authorization The new authorization credentials
	 */
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	
	
	
	// blank implemention
	
	
	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String name, Object o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAttribute(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(String username, String password) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}
}
