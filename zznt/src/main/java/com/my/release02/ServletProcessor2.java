package com.my.release02;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletProcessor2 {

	@SuppressWarnings("unchecked")
	public void process(Request request, Response response) {
		
		String uri = request.getUri();
		String servletName = uri.substring(uri.lastIndexOf("/") + 1);
		URLClassLoader loader = null;
		
		// create a URLClassLoader
		try {
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(Constants.WEB_ROOT);
			String repository = (new URL("file", null, 
					classPath.getCanonicalPath() + File.separator)).toString();
			urls[0] = new URL(null, repository, streamHandler); 
			loader = new URLClassLoader(urls);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		
		// load servlet class
		Class<Servlet> myClass = null;
		try {
			myClass = (Class<Servlet>) loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// get the servlet
		// execute the service method
		Servlet servlet = null;
		RequestFacade requestFacade = new RequestFacade(request);
		ResponseFacade responseFacade = new ResponseFacade(response);
		
		try {
			servlet = myClass.newInstance();
			// 发布request和response对象到servlet中
			servlet.service((ServletRequest) requestFacade, 
					(ServletResponse) responseFacade);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
