package com.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	public static void main(String[] args) {
		sendRequest();
	}
	
	@SuppressWarnings("static-access")
	public static void sendRequest() {
		try {
			Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
//			Socket socket = new Socket(InetAddress.getByName("192.168.200.146"), 8080);
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
			out.println("GET VR_Service/index.jsp HTTP/1.1");
			out.println("Host: localhost:8080");
			out.println("Connection: close");
			
			StringBuffer sb = new StringBuffer();
			boolean loop = true;
			while (loop) {
				int i = 0;
				while (i != -1) {
					i = in.read();
					sb.append((char) i);
				}
				loop = false;
				Thread.currentThread().sleep(50);
			}
			System.out.println(sb.toString());
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
