package com.my.release06.test;

import com.catalina.Lifecycle;
import com.catalina.LifecycleException;
import com.catalina.LifecycleListener;
import com.catalina.LifecycleSupport;

public class BComponent implements Lifecycle{
	private LifecycleSupport lifecycleSupport = new LifecycleSupport(this);
	
	
	// ------ listener manage
	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycleSupport.addLifecycleListener(listener);
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return lifecycleSupport.findLifecycleListeners();
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		lifecycleSupport.removeLifecycleListener(listener);
	}

	// fire event
	// notify listeners
	@Override
	public void start() throws LifecycleException {
		lifecycleSupport.fireLifecycleEvent(Lifecycle.BEFORE_START_EVENT, "开始了");
		
	}

	@Override
	public void stop() throws LifecycleException {
		lifecycleSupport.fireLifecycleEvent(Lifecycle.BEFORE_STOP_EVENT, "结束了");
	}
}
