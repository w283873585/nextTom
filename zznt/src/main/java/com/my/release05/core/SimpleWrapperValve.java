package com.my.release05.core;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.catalina.Contained;
import com.catalina.Container;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Valve;
import com.catalina.ValveContext;

public class SimpleWrapperValve implements Valve, Contained{
	private Container container;
	
	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {
		SimpleWrapper wrapper = (SimpleWrapper) getContainer();
		ServletRequest sreq = request.getRequest();
		ServletResponse sres = response.getResponse();
		Servlet servlet = null;
		
		HttpServletRequest hreq = null;
		if (sreq instanceof HttpServletRequest)
			hreq = (HttpServletRequest) sreq;
		HttpServletResponse hres = null; 
		if (sres instanceof HttpServletResponse) 
			hres = (HttpServletResponse) sres;
		
		try {
			servlet = wrapper.allocate();
			if (hres != null && hreq != null) {
				servlet.service(hreq, hres);
			} else {
				servlet.service(sreq, sres);
			}
		} catch (ServletException s) {}
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
