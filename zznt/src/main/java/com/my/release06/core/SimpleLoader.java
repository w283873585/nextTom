package com.my.release06.core;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import com.catalina.Container;
import com.catalina.Lifecycle;
import com.catalina.LifecycleException;
import com.catalina.LifecycleListener;
import com.catalina.Loader;

public class SimpleLoader implements Loader, Lifecycle{
	public static final String WEB_ROOT =
		    System.getProperty("user.dir") + File.separator  + "webroot";
	
	ClassLoader classLoader = null;
	Container container = null;
	
	public SimpleLoader() {
	    try {
	    	URL[] urls = new URL[1];
	    	URLStreamHandler streamHandler = null;
	    	File classPath = new File(WEB_ROOT);
	    	String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
			urls[0] = new URL(null, repository, streamHandler);
			classLoader = new URLClassLoader(urls);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		// TODO Auto-generated method stub
		
	}

	public boolean getDelegate() {
		return false;
	}

	public void setDelegate(boolean delegate) {
	}

	public String getInfo() {
		return "A simple loader";
	}

	@Override
	public boolean getReloadable() {
		return false;
	}

	@Override
	public void setReloadable(boolean reloadable) {
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	}

	@Override
	public void addRepository(String repository) {
	}

	@Override
	public String[] findRepositories() {
		return null;
	}

	@Override
	public boolean modified() {
		return false;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() throws LifecycleException {
		System.out.println("loader is starting");
	}

	@Override
	public void stop() throws LifecycleException {
		// TODO Auto-generated method stub
		
	}

}
