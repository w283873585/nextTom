package com.my.release03.connector.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;

import com.util.ParameterMap;
import com.util.RequestUtil;

public class HttpRequest {
	
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
		
		String queryString = getQueryString();
		try {
		    RequestUtil.parseParameters(results, queryString, encoding);
	    }
	    catch (UnsupportedEncodingException e) {}
		
		
	}
	
	
	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCharacterEncoding() {
		return null;
	}

	public String getRequestURI() {
		return null;
	}

	public void setQueryString(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setRequestSessionId(String substring) {
		// TODO Auto-generated method stub
		
	}

	public void setRequestedSessionURL(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setMethod(String method) {
		// TODO Auto-generated method stub
		
	}

	public void setProtocol(String protocol) {
		
	}

	public void setRequestURI(String normalizedUri) {
		// TODO Auto-generated method stub
		
	}

	public void setContentLength(int n) {
		
	}

	public void setContentType(String value) {
		// TODO Auto-generated method stub
		
	}

	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setRequestedSessionId(String value) {
		// TODO Auto-generated method stub
		
	}

	public void setRequestedSessionCookie(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void addCookie(Cookie cookie) {
		// TODO Auto-generated method stub
		
	}
}
