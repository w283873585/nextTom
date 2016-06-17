package com.my.release06.core;

import java.io.IOException;

import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import com.catalina.Container;
import com.catalina.Lifecycle;
import com.catalina.LifecycleException;
import com.catalina.LifecycleListener;
import com.catalina.LifecycleSupport;
import com.catalina.Loader;
import com.catalina.Logger;
import com.catalina.Mapper;
import com.catalina.Pipeline;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Valve;
import com.catalina.Wrapper;

public class SimpleWrapper implements Wrapper, Pipeline, Lifecycle{

	private Loader loader; 
	protected Container parent = null;
	private SimplePipeline pipeline = new SimplePipeline(this);
	
	private String name;
	private String servletClass;
	private Servlet instance = null;
	
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	protected boolean started = false;
	
	public SimpleWrapper() {
		pipeline.setBasic(new SimpleWrapperValve()); 
	}
	
	public String getInfo() {
		return "SimpleWrapper";
	}

	public Loader getLoader() {
		if (loader != null)
			return this.loader;
		if (parent != null)
			return parent.getLoader();
		return null;
	}
	
	@Override
	public void setLoader(Loader loader) {
		this.loader = loader;
	}
	
	@Override
	public void invoke(Request request, Response response) throws ServletException, IOException {
		pipeline.invoke(request, response);
	}

	@Override
	public Servlet allocate() throws ServletException {
		if (instance == null) {
			try {
				instance = loadServlet();
			} catch (ServletException e) {
				throw e;
			} catch (Throwable e) {
				throw new ServletException("Cannot allocate a servlet instance", e);
			}
		}
		return instance;
	}

	private Servlet loadServlet() throws ServletException {
		if (instance != null)
			return instance;
		
		Servlet servlet = null;
		String actualClass = servletClass;
		if (actualClass == null) {
		    throw new ServletException("servlet class has not been specified");
		}
		
		Loader loader = getLoader();
		if (loader == null)
			throw new ServletException("No loader.");
		
		ClassLoader classLoader = loader.getClassLoader();
		
		Class classClass = null;
		try {
			if (classLoader != null)
				classClass = classLoader.loadClass(actualClass);
		} catch (ClassNotFoundException e) {
			throw new ServletException("Servlet class not found");
		}
		
		try {
			servlet = (Servlet) classClass.newInstance();
		} catch (Throwable e) {
			throw new ServletException("Failed to instantiate servlet");
		}
		
		try {
			servlet.init(null);
		} catch (Throwable f) {
		      throw new ServletException("Failed initialize servlet.");
	    }
		
		return servlet;
	}

	@Override
	public void load() throws ServletException {
		instance = loadServlet();
	}
	
	
	@Override
	public long getAvailable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAvailable(long available) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getJspFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJspFile(String jspFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLoadOnStartup() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLoadOnStartup(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRunAs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRunAs(String runAs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isUnavailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addInitParameter(String name, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSecurityReference(String name, String link) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deallocate(Servlet servlet) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String findInitParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] findInitParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findSecurityReference(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] findSecurityReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeInitParameter(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSecurityReference(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unavailable(UnavailableException unavailable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload() throws ServletException {
		
	}
	
	public String getServletClass() {
		return servletClass;
	}

	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
		
	}
	
	// for pipeline
	
	public Valve getBasic() {
		return pipeline.getBasic();
	}

	public void setBasic(Valve valve) {
		pipeline.setBasic(valve);
	}

	public void addValve(Valve valve) {
		pipeline.addValve(valve);
	}

	public Valve[] getValves() {
		return pipeline.getValves();
	}

	public void removeValve(Valve valve) {
		pipeline.removeValve(valve);
	}

	@Override
	public void setName(String string) {
		this.name = string;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setParent(Container container) {
		this.parent = container;
	}

	@Override
	public Container getParent() {
		return null;
	}

	@Override
	public void setParentClassLoader(ClassLoader parent) {
	}

	@Override
	public void addChild(Container child) {
	}

	@Override
	public void addMapper(Mapper mapper) {
	}

	@Override
	public Container findChild(String name) {
		return null;
	}

	@Override
	public Container[] findChildren() {
		return null;
	}

	@Override
	public Mapper findMapper(String protocol) {
		return null;
	}

	@Override
	public Mapper[] findMappers() {
		return null;
	}

	@Override
	public Container map(Request request, boolean update) {
		return null;
	}

	@Override
	public void removeChild(Container child) {
		
	}

	@Override
	public void removeMapper(Mapper mapper) {
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
		System.out.println("Starting Wrapper " + name);
	    if (started)
	      throw new LifecycleException("Wrapper already started");

	    // Notify our interested LifecycleListeners
	    lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
	    started = true;

	    // Start our subordinate components, if any
	    if ((loader != null) && (loader instanceof Lifecycle))
	      ((Lifecycle) loader).start();

	    // Start the Valves in our pipeline (including the basic), if any
	    if (pipeline instanceof Lifecycle)
	      ((Lifecycle) pipeline).start();

	    // Notify our interested LifecycleListeners
	    lifecycle.fireLifecycleEvent(START_EVENT, null);
	    // Notify our interested LifecycleListeners
	    lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
	}

	@Override
	public void stop() throws LifecycleException {
		System.out.println("Stopping wrapper " + name);
	    // Shut down our servlet instance (if it has been initialized)
	    try {
	      instance.destroy();
	    }
	    catch (Throwable t) {
	    }
	    instance = null;
	    if (!started)
	      throw new LifecycleException("Wrapper " + name + " not started");
	    // Notify our interested LifecycleListeners
	    lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);

	    // Notify our interested LifecycleListeners
	    lifecycle.fireLifecycleEvent(STOP_EVENT, null);
	    started = false;

	    // Stop the Valves in our pipeline (including the basic), if any
	    if (pipeline instanceof Lifecycle) {
	      ((Lifecycle) pipeline).stop();
	    }

	    // Stop our subordinate components, if any
	    if ((loader != null) && (loader instanceof Lifecycle)) {
	      ((Lifecycle) loader).stop();
	    }

	    // Notify our interested LifecycleListeners
	    lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
		
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogger(Logger logger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DirContext getResources() {
		// TODO Auto-generated method stub
		return null;
	}
}
