package com.my.release05.core;

import java.io.IOException;

import javax.servlet.ServletException;

import com.catalina.Contained;
import com.catalina.Container;
import com.catalina.Pipeline;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Valve;
import com.catalina.ValveContext;

public class SimplePipeline implements Pipeline{

	protected Valve basic = null;
	protected Valve[] valves = new Valve[0];
	protected Container container = null;
	
	public SimplePipeline(Container container) {
		this.container = container;
	}
	
	@Override
	public Valve getBasic() {
		return basic;
	}

	@Override
	public void setBasic(Valve valve) {
		this.basic = valve;
		((Contained) valve).setContainer(container);
	}

	@Override
	public void addValve(Valve valve) {
		if (valve instanceof Contained)
		      ((Contained) valve).setContainer(this.container);

	    synchronized (valves) {
	      Valve results[] = new Valve[valves.length +1];
	      System.arraycopy(valves, 0, results, 0, valves.length);
	      results[valves.length] = valve;
	      valves = results;
	    }
	}

	@Override
	public Valve[] getValves() {
		return valves;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		// Invoke the first Valve in this pipeline for this request
		new SimplePipelineValveContext().invokeNext(request, response);
	}

	@Override
	public void removeValve(Valve valve) {}
	
	protected class SimplePipelineValveContext implements ValveContext {

		private int stage = 0;
		
		public String getInfo() {
			return "a valveContext";
		}

		public void invokeNext(Request request, Response response) throws IOException, ServletException {
			int subscript = stage;
			stage = stage + 1;
			
			if (subscript < valves.length) {
				valves[subscript].invoke(request, response, this);
			} else if (subscript == valves.length && basic != null){
				basic.invoke(request, response, this);
			} else {
				throw new ServletException("no valve");
			}
		}
	}
}
