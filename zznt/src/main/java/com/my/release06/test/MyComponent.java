package com.my.release06.test;

import java.util.ArrayList;

import com.catalina.Lifecycle;
import com.catalina.LifecycleEvent;
import com.catalina.LifecycleException;
import com.catalina.LifecycleListener;

public class MyComponent implements Lifecycle{
	private ArrayList<LifecycleListener> listeners = new ArrayList<LifecycleListener>();
	
	
	// ------ listener manage
	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		listeners.add(listener);
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return (LifecycleListener[]) listeners.toArray();
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		listeners.remove(listener);
	}

	// fire event
	// notify listeners
	@Override
	public void start() throws LifecycleException {
		for (LifecycleListener listener : listeners)
			listener.lifecycleEvent(new LifecycleEvent(this, Lifecycle.BEFORE_START_EVENT, "开始了"));
	}

	@Override
	public void stop() throws LifecycleException {
		for (LifecycleListener listener : listeners)
			listener.lifecycleEvent(new LifecycleEvent(this, Lifecycle.BEFORE_START_EVENT, "开始了"));
	}
}
