package com.my.release01;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
	private InputStream input;
	private String uri;
	
	public Request(InputStream input) {
		this.input = input;
	}

	public void parse() {
		StringBuffer request = new StringBuffer();
		int i;
		byte[] buffer = new byte[2048];
		
		try {
			i = input.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			i = -1;
		}
		
		for (int j = 0; j < i; j++) {
			request.append((char) buffer[j]);
		}
		
		// print request string
		System.out.println(request.toString());
	
		// print the uri
		System.out.println(parseUri_t(request.toString()));
		uri = parseUri(request.toString());
	}

	public String getUri() {
		return uri;
	}
	
	private String parseUri(String requestString) {
		int index1, index2; 
		index1 = requestString.indexOf(' '); 
		if (index1 != -1) { 
			index2 = requestString.indexOf(' ', index1 + 1); 
			if (index2 > index1) 
				return requestString.substring(index1 + 1, index2); 
		}
		return null;
	}
	
	private String parseUri_t(String str) {
		String result = "";
		Pattern patt = Pattern.compile("\\s([\\S]*)\\s+HTTP");
		Matcher matcher = patt.matcher(str);
		if (matcher.find()) {
			result = matcher.group(1);
		}
		return result;
	}
}
