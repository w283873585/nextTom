package com.my.release04.connector.http;

import java.net.Socket;

import com.catalina.Globals;
import com.catalina.Lifecycle;
import com.catalina.LifecycleException;
import com.catalina.LifecycleListener;
import com.util.ServerInfo;
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
	
	
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
	// ------------------------------------------------ lifeCycle methods
	
	public LifecycleListener[] findLifecycleListeners() {
		return null;
	}
	public void addLifecycleListener(LifecycleListener listener) {}
	public void removeLifecycleListener(LifecycleListener listener) {}
	public void start() throws LifecycleException {}
	public void stop() throws LifecycleException {}
}
