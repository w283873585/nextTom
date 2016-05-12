package com.my.release01;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer {
	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "src\\main\\webroot";
	
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	
	boolean shutdown = false;
	
	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 10, InetAddress.getByName("127.0.0.1"));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// Loop waiting for a request
		while (!shutdown) {
			try {
				Socket socket = null;
				InputStream input = null;
				OutputStream out = null;
				
				socket = serverSocket.accept();
				input = socket.getInputStream();
				
				// create request object
				Request request = new Request(input);
				request.parse();
				
				// create response object
				Response response = new Response(out);
				response.setRequest(request);
				response.sendStaticResource();
				
				// close socket
				socket.close();
				
				// check if the previous URI is a shutdown command
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
				
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	
	public static void main(String[] args) {
		new HttpServer().await();
	}
}
