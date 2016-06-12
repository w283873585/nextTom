package com.my.release04.connector.http;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessControlException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Stack;
import java.util.Vector;

import com.catalina.Connector;
import com.catalina.Container;
import com.catalina.DefaultServerSocketFactory;
import com.catalina.Lifecycle;
import com.catalina.LifecycleException;
import com.catalina.LifecycleListener;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.ServerSocketFactory;
import com.catalina.Service;
import com.util.StringManager;

public class HttpConnector implements Runnable, Connector, Lifecycle {
	
	// another component
	private Service service = null;
	private Container container = null;
	
	// normal attribute
	private int debug = 0;
	private int bufferSize = 2048;
	private static final String info =
			"org.apache.catalina.connector.http.HttpConnector/1.0";
	
	// serversocket
	private ServerSocket serverSocket = null;
	private ServerSocketFactory factory = null;
	private int port = 8080;
	private String address = null;
	private int connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;
	
	// HTTP Properties
	private String scheme = "http";
	private boolean secure = false;
	private int proxyPort = 0;
	private String proxyName = null;
	private int redirectPort = 443;
	private int acceptCount = 10;
	private boolean allowChunking = true;
	private boolean tcpNoDelay = true;
	private boolean enableLookups = false;
	
	// about processors
	private Vector created = new Vector();
	private Stack processors = new Stack();
	private int curProcessors = 0;
	protected int minProcessors = 5;
	private int maxProcessors = 20;
	
	// about lifecycle
	private boolean initialized = false;
    private boolean started = false;
    private boolean stopped = false;
	
    // about background thread
    private Thread thread = null;
    private String threadName = null;
    private Object threadSync = new Object();
    
    // about errorInfo
    private StringManager sm =
	        StringManager.getManager(Constants.Package);
    
    
	public HttpConnector() {
		super();
	}

	public ServerSocketFactory getFactory() {
		if (this.factory == null) {
            synchronized (this) {
                this.factory = new DefaultServerSocketFactory();
            }
        }
        return (this.factory);
	}
	
	
	
	// ---------------------------------------------- Lifecycle Methods
	
	public void initialize() throws LifecycleException {
		if (initialized) {
			throw new LifecycleException (
	                sm.getString("httpConnector.alreadyInitialized"));
		}
		
		this.initialized = true;
		Exception eRethrow = null;
		
		// Establish a server socket on the specified port
        try {
            serverSocket = open();
        } catch (IOException ioe) {
            log("httpConnector, io problem: ", ioe);
            eRethrow = ioe;
        } catch (KeyStoreException kse) {
            log("httpConnector, keystore problem: ", kse);
            eRethrow = kse;
        } catch (NoSuchAlgorithmException nsae) {
            log("httpConnector, keystore algorithm problem: ", nsae);
            eRethrow = nsae;
        } catch (CertificateException ce) {
            log("httpConnector, certificate problem: ", ce);
            eRethrow = ce;
        } catch (UnrecoverableKeyException uke) {
            log("httpConnector, unrecoverable key: ", uke);
            eRethrow = uke;
        } catch (KeyManagementException kme) {
            log("httpConnector, key management problem: ", kme);
            eRethrow = kme;
        }
        
        if ( eRethrow != null )
            throw new LifecycleException(threadName + ".open", eRethrow);
	}
	
	
	public void start() throws LifecycleException {
		
		if (started)
			throw new LifecycleException
            (sm.getString("httpConnector.alreadyStarted"));
		threadName = "HttpConnector[" + port + "]";
		started = true;
		
		// start our backgroud thread
		threadStart();
		
		// Create the specified minimum number of processors
        while (curProcessors < minProcessors) {
            if ((maxProcessors > 0) && (curProcessors >= maxProcessors))
                break;
            HttpProcessor processor = newProcessor();
            recycle(processor);
        }
	}
	
	public void stop() throws LifecycleException {
		// Validate and update our current state
        if (!started)
            throw new LifecycleException
                (sm.getString("httpConnector.notStarted"));
        started = false;

        // Gracefully shut down all processors we have created
        for (int i = created.size() - 1; i >= 0; i--) {
            HttpProcessor processor = (HttpProcessor) created.elementAt(i);
            if (processor instanceof Lifecycle) {
                try {
                    ((Lifecycle) processor).stop();
                } catch (LifecycleException e) {
                    log("HttpConnector.stop", e);
                }
            }
        }

        synchronized (threadSync) {
            // Close the server socket we were using
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    ;
                }
            }
            // Stop our background thread
            threadStop();
        }
        serverSocket = null;
	}
	
	
	
	// ---------------------------------------------- about HttpProcessor Methods
	void recycle(HttpProcessor processor) {
		processors.push(processor);
	}
	private HttpProcessor newProcessor() {
		HttpProcessor processor = new HttpProcessor(this, curProcessors++);
		if (processor instanceof Lifecycle) {
			try {
				((Lifecycle) processor).start();
			} catch (LifecycleException e) {
				return null;
			}
		}
		created.addElement(processor);
		return processor;
	}
	
	private HttpProcessor createProcessor() {

        synchronized (processors) {
            if (processors.size() > 0)
                return ((HttpProcessor) processors.pop());
            if ((maxProcessors > 0) && (curProcessors < maxProcessors)) {
                return (newProcessor());
            } else {
                if (maxProcessors < 0) {
                    return (newProcessor());
                } else {
                    return (null);
                }
            }
        }
    }
	
	// ----------------------------------------------- private methods 
	
	private ServerSocket open() 
			throws IOException, KeyStoreException, NoSuchAlgorithmException,
	           CertificateException, UnrecoverableKeyException,
	           KeyManagementException {
		 // Acquire the server socket factory for this Connector
		ServerSocketFactory factory = getFactory();
		
		// If no address is specified, open a connection on all addresses
        if (address == null) {
            log(sm.getString("httpConnector.allAddresses"));
            try {
                return (factory.createSocket(port, acceptCount));
            } catch (BindException be) {
                throw new BindException(be.getMessage() + ":" + port);
            }
        }

        // Open a server socket on the specified address
        try {
            InetAddress is = InetAddress.getByName(address);
            log(sm.getString("httpConnector.anAddress", address));
            try {
                return (factory.createSocket(port, acceptCount, is));
            } catch (BindException be) {
                throw new BindException(be.getMessage() + ":" + address +
                                        ":" + port);
            }
        } catch (Exception e) {
            log(sm.getString("httpConnector.noAddress", address));
            try {
                return (factory.createSocket(port, acceptCount));
            } catch (BindException be) {
                throw new BindException(be.getMessage() + ":" + port);
            }
        }
	}
	
	// ---------------------------------------------- Background Thread Methods 
	
	private void threadStart() {
		
		log(sm.getString("httpConnector.starting"));

        thread = new Thread(this, threadName);
        thread.setDaemon(true);
        thread.start();
	}
	
	/**
     * Stop the background processing thread.
     */
    private void threadStop() {

        log(sm.getString("httpConnector.stopping"));

        stopped = true;
        try {
            threadSync.wait(5000);
        } catch (InterruptedException e) {
            ;
        }
        thread = null;
    }
	
    public void run() {
		// loop util we receive a shutdown command
    	while (!stopped) {
    		
    		// accept the next incoming connection from the server socket
    		Socket socket = null;
    		try {
				socket = serverSocket.accept();
				if (connectionTimeout > 0) 
					socket.setSoTimeout(connectionTimeout);
			} catch (AccessControlException ace) {
				log("socket accept security exception: " + ace.getMessage());
                continue;
			} catch (IOException e) {
				if (started && !stopped)
                    log("accept: ", e);
                break;
			}
    		
    		// Hand this socket off to an appropriate processor
    		HttpProcessor processor = createProcessor();
            if (processor == null) {
                try {
                    log(sm.getString("httpConnector.noProcessor"));
                    socket.close();
                } catch (IOException e) {
                    ;
                }
                continue;
            }
            processor.assign(socket);
            
            // The processor will recycle itself when it finishes
    	}
    	
    	// Notify the threadStop() method that we have shut ourselves down
        synchronized (threadSync) {
            threadSync.notifyAll();
        }
	}
    
	
	private void log(String info) {
		System.out.println(info);
	}
	
	private void log(String string, Exception e) {
		log(string);
	}
	
	public String getScheme() {
		return scheme;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public boolean getEnableLookups() {
		return this.enableLookups;
	}

	public void setEnableLookups(boolean enableLookups) {
		this.enableLookups = enableLookups;
	}

	public void setFactory(ServerSocketFactory factory) {
		this.factory = factory;
	}

	public String getInfo() {
		return info;
	}

	public int getRedirectPort() {
		return this.redirectPort;
	}

	public void setRedirectPort(int redirectPort) {
		this.redirectPort = redirectPort; 
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public boolean getSecure() {
		return this.secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public Service getService() {
		return this.service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	public int getAcceptCount() {
		return acceptCount;
	}
	
	public void setAcceptCount(int count) {
        this.acceptCount = count;
    }
	
	public boolean isChunkingAllowed() {
        return (allowChunking);
    }
	
	public boolean getAllowChunking() {
        return isChunkingAllowed();
    }
	
	public void setAllowChunking(boolean allowChunking) {
        this.allowChunking = allowChunking;
    }
	
    public String getAddress() {
        return (this.address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isAvailable() {
        return (started);
    }
    
    public int getBufferSize() {
        return (this.bufferSize);
    }
    
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    public int getCurProcessors() {
        return (curProcessors);
    }
	
    public int getDebug() {
        return (debug);
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }
    
    public int getMinProcessors() {
        return (minProcessors);
    }
    
    public void setMinProcessors(int minProcessors) {
        this.minProcessors = minProcessors;
    }
    
    public int getMaxProcessors() {
        return (maxProcessors);
    }
    
    public void setMaxProcessors(int maxProcessors) {
        this.maxProcessors = maxProcessors;
    }
    
    public int getPort() {
        return (this.port);
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    /**
     * Return the proxy server name for this Connector.
     */
    public String getProxyName() {

        return (this.proxyName);

    }


    /**
     * Set the proxy server name for this Connector.
     *
     * @param proxyName The new proxy server name
     */
    public void setProxyName(String proxyName) {

        this.proxyName = proxyName;

    }


    /**
     * Return the proxy server port for this Connector.
     */
    public int getProxyPort() {

        return (this.proxyPort);

    }


    /**
     * Set the proxy server port for this Connector.
     *
     * @param proxyPort The new proxy server port
     */
    public void setProxyPort(int proxyPort) {

        this.proxyPort = proxyPort;

    }

    /**
     * Return the TCP no delay flag value.
     */
    public boolean getTcpNoDelay() {

        return (this.tcpNoDelay);

    }


    /**
     * Set the TCP no delay flag which will be set on the socket after
     * accepting a connection.
     *
     * @param tcpNoDelay The new TCP no delay flag
     */
    public void setTcpNoDelay(boolean tcpNoDelay) {

        this.tcpNoDelay = tcpNoDelay;

    }
    
	@Override
	public Request createRequest() {
		HttpRequestImpl request = new HttpRequestImpl();
        request.setConnector(this);
        return (request);
	}


	@Override
	public Response createResponse() {
		HttpResponseImpl response = new HttpResponseImpl();
        response.setConnector(this);
        return (response);
	}

	public void addLifecycleListener(LifecycleListener listener) {}

	public LifecycleListener[] findLifecycleListeners() {
		return null;
	}

	public void removeLifecycleListener(LifecycleListener listener) {}
}
