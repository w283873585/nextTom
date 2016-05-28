package com.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
			Socket socket = new Socket(InetAddress.getByName("192.168.200.146"), 8080);
//			Socket socket = new Socket(InetAddress.getByName("192.168.200.146"), 8080);
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), false);
			InputStream is = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
		
			out.println("GET /vrshow/dev HTTP/1.1");
			out.println("Host: http://192.168.200.146:8080");
			out.println("Connection: close");
			out.flush();
			// out.close();
			
			StringBuffer sb = new StringBuffer();
			boolean loop = true;
			while (loop) {
				int i = 0;
				while (i != -1) {
					i = is.read();
					System.out.println(" ’µΩ¡À" + i);
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
