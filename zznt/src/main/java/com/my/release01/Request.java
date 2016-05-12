package com.my.release01;

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
		
	}

	public String getUri() {
		return uri;
	}
	
	public String getResourceName(String str) {
		String result = "";
		Pattern patt = Pattern.compile("\\s([\\S]*)\\s+HTTP");
		Matcher matcher = patt.matcher(str);
		if (matcher.find()) {
			result = matcher.group(1);
		}
		return result;
	}
}
