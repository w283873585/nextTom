package com.my.release05.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.catalina.Container;
import com.catalina.Context;
import com.catalina.Loader;
import com.catalina.Manager;
import com.catalina.Mapper;
import com.catalina.Pipeline;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Valve;
import com.catalina.Wrapper;
import com.util.CharsetMapper;

public class SimpleContext implements Context, Pipeline {
	
	public SimpleContext() {
		pipeline.setBasic(new SimpleContextValve());
	}
	
	protected HashMap children = new HashMap();
	protected Loader loader = null;
	protected SimplePipeline pipeline = new SimplePipeline(this);
	protected HashMap servletMappings = new HashMap();
	protected Mapper mapper = null;
	protected HashMap mappers = new HashMap();
	private Container parent = null;
	
	@Override
	public String getInfo() {
		return "main context";
	}

	@Override
	public void invoke(Request request, Response response) throws ServletException, IOException {
		pipeline.invoke(request, response);
	}

	@Override
	public Loader getLoader() {
		return loader;
	}

	@Override
	public Object[] getApplicationListeners() {
		return null;
	}

	@Override
	public void setApplicationListeners(Object[] listeners) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean getAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAvailable(boolean available) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Manager getManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getCookies() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CharsetMapper getCharsetMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChild(Wrapper child) {
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
	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	@Override
	public void addServletMapping(String pattern, String name) {
		synchronized (servletMappings) {
	      servletMappings.put(pattern, name);
	    }
	}

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
	public Wrapper map(Request request, boolean update) {
		//this method is taken from the map method in org.apache.cataline.core.ContainerBase
	    //the findMapper method always returns the default mapper, if any, regardless the
	    //request's protocol
	    Mapper mapper = findMapper(request.getRequest().getProtocol());
	    if (mapper == null)
	      return (null);

	    // Use this Mapper to perform this mapping
	    return (mapper.map(request, update));
	}

	
	// method implementations of Pipeline
	  public Valve getBasic() {
	    return pipeline.getBasic();
	  }

	  public void setBasic(Valve valve) {
	    pipeline.setBasic(valve);
	  }

	  public synchronized void addValve(Valve valve) {
	    pipeline.addValve(valve);
	  }

	  public Valve[] getValves() {
	    return pipeline.getValves();
	  }

	  public void removeValve(Valve valve) {
	    pipeline.removeValve(valve);
	  }

	public Wrapper findChild(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String findServletMapping(String relativeURI) {
		// TODO Auto-generated method stub
		return null;
	}

}
