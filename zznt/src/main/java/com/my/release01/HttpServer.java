package com.my.release01;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class HttpServer {
	boolean isShutdwon = false;
	public void await() {
		try {
			ServerSocket server = new ServerSocket(8080, 10, InetAddress.getByName("127.0.0.1"));
			while (!isShutdwon) {
				Socket socket = server.accept();
				InputStream is = socket.getInputStream();
				byte[] buff = new byte[1024];
				int i = 0;
				StringBuilder build = new StringBuilder();
				while (i != -1) {
					i = is.read(buff);
					build.append(new String(buff));
				}
				
				
				
				is.close();
			}
			server.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getURL(String str) {
		Pattern patt = Pattern.compile("\\s(.*)\\s*HTTP.*\n");
		Matcher m = patt.matcher(str);
		return null;
	}
	
	public static void main(String[] args) {
		new HttpServer().await();
	}
}
