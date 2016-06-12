package com.my.release05.valves;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.catalina.Contained;
import com.catalina.Container;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Valve;
import com.catalina.ValveContext;

public class HeaderLoggerValve implements Valve, Contained {

	private Container container;
	@Override
	public String getInfo() {
		return "HeaderLoggerValve";
	}

	public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
		// Pass this request on to the next valve in our pipeline 
		valveContext.invokeNext(request, response); 
		System.out.println("Client IP Logger Valve"); 
		ServletRequest sreq = request.getRequest(); 
		System.out.println(sreq.getRemoteAddr());
		System.out.println("-----------------------------------");
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
