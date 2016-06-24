package com.my.release10.core;

import java.io.IOException;
import java.util.HashMap;

import javax.naming.directory.DirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.catalina.Container;
import com.catalina.Context;
import com.catalina.Lifecycle;
import com.catalina.LifecycleException;
import com.catalina.LifecycleListener;
import com.catalina.LifecycleSupport;
import com.catalina.Loader;
import com.catalina.Logger;
import com.catalina.Manager;
import com.catalina.Mapper;
import com.catalina.Pipeline;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Valve;
import com.my.release07.core.SimpleContextValve;
import com.util.CharsetMapper;
import com.util.ContextBase;

public class SimpleContext extends ContextBase implements Context, Pipeline, Lifecycle {

	public SimpleContext() {
	    pipeline.setBasic(new SimpleContextValve());
	}
	
	protected HashMap children = new HashMap();
	private Loader loader = null;
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	private SimplePipeline pipeline = new SimplePipeline(this);
	private HashMap servletMappings = new HashMap();
	protected Mapper mapper = null;
	protected HashMap mappers = new HashMap();
	private Container parent = null;
	protected boolean started = false;
	
	private Logger logger = null;
	
	
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public String findServletMapping(String pattern) {
	    synchronized (servletMappings) {
	      return ((String) servletMappings.get(pattern));
	    }
	  }
	
	
	@Override
	public Loader getLoader() {
		if (loader != null)
		      return (loader);
	    if (parent != null)
	      return (parent.getLoader());
	    return (null);
	}

	@Override
	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	public String getName() { return null; }
	public void setName(String name) {}
	
	public Container getParent() {
		return parent;
	}
	public void setParent(Container parent) {
		this.parent = parent;
	}

	@Override
	public void setParentClassLoader(ClassLoader classLoader) {}

	@Override
	public void addChild(Container child) {
		child.setParent((Container) this);
	    children.put(child.getName(), child);
	}

	@Override
	public void addMapper(Mapper mapper) {
		// this method is adopted from addMapper in ContainerBase
	    // the first mapper added becomes the default mapper
	    mapper.setContainer((Container) this);      // May throw IAE
	    this.mapper = mapper;
	    synchronized(mappers) {
	      if (mappers.get(mapper.getProtocol()) != null)
	        throw new IllegalArgumentException("addMapper:  Protocol '" +
	          mapper.getProtocol() + "' is not unique");
	      mapper.setContainer((Container) this);      // May throw IAE
	      mappers.put(mapper.getProtocol(), mapper);
	      if (mappers.size() == 1)
	        this.mapper = mapper;
	      else
	        this.mapper = null;
	    }
	}

	@Override
	public Container findChild(String name) {
		if (name == null)
	      return (null);
	    synchronized (children) {       // Required by post-start changes
	      return ((Container) children.get(name));
	    }
	}

	@Override
	public Container[] findChildren() {
		synchronized (children) {
	      Container results[] = new Container[children.size()];
	      return ((Container[]) children.values().toArray(results));
	    }
	}

	@Override
	public Mapper findMapper(String protocol) {
		// the default mapper will always be returned, if any,
	    // regardless the value of protocol
	    if (mapper != null)
	      return (mapper);
	    else
	      synchronized (mappers) {
	        return ((Mapper) mappers.get(protocol));
	    }
	}

	@Override
	public Mapper[] findMappers() {
		return null;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		 pipeline.invoke(request, response);
	}

	@Override
	public Container map(Request request, boolean update) {
		//this method is taken from the map method in org.apache.cataline.core.ContainerBase
	    //the findMapper method always returns the default mapper, if any, regardless the
	    //request's protocol
	    Mapper mapper = findMapper(request.getRequest().getProtocol());
	    if (mapper == null)
	      return (null);

	    // Use this Mapper to perform this mapping
	    return (mapper.map(request, update));
	}

	@Override
	public void removeChild(Container child) {

	}

	@Override
	public void removeMapper(Mapper mapper) {

	}

	@Override
	public Object[] getApplicationListeners() {
		return null;
	}

	@Override
	public void setApplicationListeners(Object[] listeners) {
	}

	@Override
	public boolean getAvailable() {
		return false;
	}

	@Override
	public void setAvailable(boolean available) {

	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public Manager getManager() {
		return null;
	}

	@Override
	public boolean getCookies() {
		return false;
	}

	@Override
	public CharsetMapper getCharsetMapper() {
		return null;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public void addServletMapping(String pattern, String name) {
		synchronized (servletMappings) {
			servletMappings.put(pattern, name);
	    }
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycle.addLifecycleListener(listener);
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return lifecycle.findLifecycleListeners();
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		lifecycle.removeLifecycleListener(listener);
	}

	@Override
	public void start() throws LifecycleException {
		
		System.out.println(System.getProperty("user.dir"));
		log("context started");
		
		if (started)
		      throw new LifecycleException("SimpleContext has already started");
		
		// Notify our interested LifecycleListeners
	    lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
	    started = true;
	    try {
	      // Start our subordinate components, if any
	      if ((loader != null) && (loader instanceof Lifecycle))
	        ((Lifecycle) loader).start();

	      // Start our child containers, if any
	      Container children[] = findChildren();
	      for (int i = 0; i < children.length; i++) {
	        if (children[i] instanceof Lifecycle)
	          ((Lifecycle) children[i]).start();
	      }

	      // Start the Valves in our pipeline (including the basic),
	      // if any
	      if (pipeline instanceof Lifecycle)
	        ((Lifecycle) pipeline).start();
	      // Notify our interested LifecycleListeners
	      lifecycle.fireLifecycleEvent(START_EVENT, null);
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }

	    // Notify our interested LifecycleListeners
	    lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
	}

	@Override
	public void stop() throws LifecycleException {
		 if (!started)
		      throw new LifecycleException("SimpleContext has not been started");
		    // Notify our interested LifecycleListeners
		    lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
		    lifecycle.fireLifecycleEvent(STOP_EVENT, null);
		    started = false;
		    try {
		      // Stop the Valves in our pipeline (including the basic), if any
		      if (pipeline instanceof Lifecycle) {
		        ((Lifecycle) pipeline).stop();
		      }

		      // Stop our child containers, if any
		      Container children[] = findChildren();
		      for (int i = 0; i < children.length; i++) {
		        if (children[i] instanceof Lifecycle)
		          ((Lifecycle) children[i]).stop();
		      }
		      if ((loader != null) && (loader instanceof Lifecycle)) {
		        ((Lifecycle) loader).stop();
		      }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }
		    // Notify our interested LifecycleListeners
		    lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
		
	}

	@Override
	public Valve getBasic() {
		return pipeline.getBasic();
	}

	@Override
	public void setBasic(Valve valve) {
		pipeline.setBasic(valve);
	}

	@Override
	public void addValve(Valve valve) {
		pipeline.addValve(valve);
		
	}

	@Override
	public Valve[] getValves() {
		return pipeline.getValves();
	}

	@Override
	public void removeValve(Valve valve) {
		pipeline.removeValve(valve);
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	private void log(String msg) {
		if (this.logger != null)
			logger.log(msg);
	}

	@Override
	public DirContext getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getReloadable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSessionTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Pipeline getPipeline() {
		return this.pipeline;
	}
}
