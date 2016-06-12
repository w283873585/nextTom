package com.my.release05.valves;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.catalina.Contained;
import com.catalina.Container;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Valve;
import com.catalina.ValveContext;

public class ClientIPLoggerValve implements Valve, Contained {


	private Container container;
	
	@Override
	public String getInfo() {
		return "ClientIPLoggerValve";
	}

	public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
		// Pass this request on to the next valve in our pipeline 
		valveContext.invokeNext(request, response); 
		System.out.println("Header Logger Valve"); 
		ServletRequest sreq = request.getRequest(); 
		if (sreq instanceof HttpServletRequest) { 
			HttpServletRequest hreq = (HttpServletRequest) sreq; 
			Enumeration headerNames = hreq.getHeaderNames(); 
			while (headerNames.hasMoreElements()) { 
				String headerName = headerNames.nextElement().toString();
				String headerValue = hreq.getHeader(headerName); 
				System.out.println(headerName + ":" + headerValue); 
			} 
		} else 
			System.out.println("Not an HTTP Request"); 
		
		System.out.println ("-----------------------------------");
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

}
