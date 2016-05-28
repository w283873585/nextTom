package com.my.release04.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.catalina.Connector;
import com.catalina.Container;
import com.catalina.LifecycleException;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.ServerSocketFactory;
import com.catalina.Service;

public class HttpConnector implements Runnable, Connector {
	private Container container;
	
	public HttpConnector() throws Exception {
		super();
	}

	boolean stopped;
	private String scheme = "http";
	
	public String getScheme() {
		return scheme;
	}
	
	
	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		int port = 8080;
		
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		while (!stopped) {
			// Accept the next incoming connection from the server socket
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				System.out.println("请求进来了。。。");
			} catch (Exception e) {
				continue;
			}
			
			// Hand this socket off to an HttpProcessor
			HttpProcessor processor = new HttpProcessor(this);
			processor.process(socket);
		}
	}


	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}


	@Override
	public boolean getEnableLookups() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setEnableLookups(boolean enableLookups) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ServerSocketFactory getFactory() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setFactory(ServerSocketFactory factory) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getRedirectPort() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setRedirectPort(int redirectPort) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setScheme(String scheme) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean getSecure() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setSecure(boolean secure) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setService(Service service) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Request createRequest() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response createResponse() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void initialize() throws LifecycleException {
		// TODO Auto-generated method stub
		
	}
}
