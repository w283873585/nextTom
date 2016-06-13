package com.my.release07.core;

import javax.servlet.http.HttpServletRequest;

import com.catalina.Container;
import com.catalina.HttpRequest;
import com.catalina.Mapper;
import com.catalina.Request;
import com.catalina.Wrapper;

public class SimpleContextMapper implements Mapper {

	private String protocol;
	private SimpleContext context = null;
	
	public Container getContainer() {
		return context;
	}

	public void setContainer(Container container) {
		if (!(container instanceof SimpleContext))
		      throw new IllegalArgumentException("Illegal type of container");
		context = (SimpleContext) container;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Wrapper map(Request request, boolean update) {
		// Identify the context-relative URI to be mapped
	    String contextPath =
	      ((HttpServletRequest) request.getRequest()).getContextPath();
	    String requestURI = ((HttpRequest) request).getDecodedRequestURI();
	    String relativeURI = requestURI.substring(contextPath.length());
	    // Apply the standard request URI mapping rules from the specification
	    Wrapper wrapper = null;
	    String servletPath = relativeURI;
	    String pathInfo = null;
	    String name = context.findServletMapping(relativeURI);
	    if (name != null)
	      wrapper = (Wrapper) context.findChild(name);
	    return (wrapper);
	}
}
