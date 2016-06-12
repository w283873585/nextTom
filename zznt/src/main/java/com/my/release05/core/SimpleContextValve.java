package com.my.release05.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.catalina.Contained;
import com.catalina.Container;
import com.catalina.Context;
import com.catalina.HttpRequest;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Valve;
import com.catalina.ValveContext;
import com.catalina.Wrapper;

public class SimpleContextValve implements Valve, Contained{

	protected Container container;
	
	@Override
	public String getInfo() {
		return "context default valve";
	}

	@Override
	public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
		// Validate the request and response object types
	    if (!(request.getRequest() instanceof HttpServletRequest) ||
	      !(response.getResponse() instanceof HttpServletResponse)) {
	      return;     // NOTE - Not much else we can do generically
	    }
	    
	    // Disallow any direct access to resources under WEB-INF or META-INF
	    HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
	    String contextPath = hreq.getContextPath();
	    String requestURI = ((HttpRequest) request).getDecodedRequestURI();
	    String relativeURI =
	      requestURI.substring(contextPath.length()).toUpperCase();
	    
	    Context context = (Context) getContainer();
	    // Select the Wrapper to be used for this Request
	    Wrapper wrapper = null;
	    try {
	      wrapper = (Wrapper) context.map(request, true);
	    }
	    catch (IllegalArgumentException e) {
	      badRequest(requestURI, (HttpServletResponse) response.getResponse());
	      return;
	    }
	    
	    if (wrapper == null) {
	        notFound(requestURI, (HttpServletResponse) response.getResponse());
	        return;
	    }
	    // Ask this Wrapper to process this Request
	    response.setContext(context);
	    wrapper.invoke(request, response);
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	
	private void badRequest(String requestURI, HttpServletResponse response) {
	    try {
	      response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
	    }
	    catch (IllegalStateException e) {
	      ;
	    }
	    catch (IOException e) {
	      ;
	    }
	  }

	 private void notFound(String requestURI, HttpServletResponse response) {
	    try {
	      response.sendError(HttpServletResponse.SC_NOT_FOUND, requestURI);
	    }
	    catch (IllegalStateException e) {
	      ;
	    }
	    catch (IOException e) {
	      ;
	    }
	}
}
