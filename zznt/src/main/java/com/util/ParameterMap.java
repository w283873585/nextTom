package com.util;

import java.util.HashMap;
import java.util.Map;

import com.my.release03.StringManager;

@SuppressWarnings({ "serial", "rawtypes" })
public final class ParameterMap extends HashMap{
	public ParameterMap(){
		super();
	}
	
	public ParameterMap(int initialCapacity) {
		super(initialCapacity);
	}
	
	public ParameterMap(int initialCapacity, float loadFactor) { 
		super(initialCapacity, loadFactor); 
	}
	
	@SuppressWarnings("unchecked")
	public ParameterMap(Map map) { 
		super(map); 
	}
	
	private boolean locked;
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	private static final StringManager sm = StringManager.getManager("com.util");
	
	public void clear() {
		if (locked) {
			throw new IllegalStateException(sm.getString("parameterMap.locked"));
		}
		super.clear();
	}
	
	@SuppressWarnings("unchecked")
	public Object put(Object key, Object value) { 
		if (locked) 
			throw new IllegalStateException (sm.getString("parameterMap.locked")); 
		return (super.put(key, value)); 
	}
	
	@SuppressWarnings({ "unchecked" })
	public void putAll(Map map) { 
		if (locked) 
			throw new IllegalStateException (sm.getString("parameterMap.locked")); 
		super.putAll(map); 
	} 
	public Object remove(Object key) { 
		if (locked) 
			throw new IllegalStateException (sm.getString("parameterMap.locked")); 
		return (super.remove(key)); }
}
