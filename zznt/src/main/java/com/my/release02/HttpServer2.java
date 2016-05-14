package com.my.release02;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer2 {
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	
	private boolean shutdown = false;
	
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
			Socket socket = null;
			InputStream input = null;
			OutputStream out = null;
			
			try {
				
				socket = serverSocket.accept();
				input = socket.getInputStream();
				out = socket.getOutputStream();
				
				// create request object
				Request request = new Request(input);
				request.parse();
				
				// create response object
				Response response = new Response(out);
				response.setRequest(request);


				// check if this is a request for a servlet or 
				// a static resource 
				// a request for a servlet begins with "/servlet/"
				if (request.getUri().startsWith("/servlet/")) {
					ServletProcessor2 processor = new ServletProcessor2(); 
					processor.process(request, response); 
				} else {
					StaticResoureProcessor processor = new StaticResoureProcessor(); 
					processor.process(request, response); 
				}
				
				// close socket
				socket.close();
				
				// check if the previous URI is a shutdown command
				shutdown = SHUTDOWN_COMMAND.equals(request.getUri());
				
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public static void main(String[] args) {
		new HttpServer2().await();
	}
}
