package com.util;

import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class StringManager {
	
	private ResourceBundle bundle;
	
	private StringManager(String packageName) {
		String bundleName = packageName + ".LocalStrings";
		bundle = ResourceBundle.getBundle(bundleName);
	}
	
	public String getString(String key) {
		if (key == null) {
			String msg = "key is null";
			
			throw new NullPointerException(msg);
		}
		
		String str = null;
		
		try {
		    str = bundle.getString(key);
        } catch (MissingResourceException mre) {
            str = "Cannot find message associated with key '" + key + "'";
        }
		
		return str;
	}
	
	private static Hashtable<String, StringManager> managers = new Hashtable<String, StringManager>();
	
	public synchronized static StringManager getManager(String packageName) {
		StringManager mgr = (StringManager)managers.get(packageName);
		if (mgr == null) {
		    mgr = new StringManager(packageName);
		    managers.put(packageName, mgr);
		}
		return mgr;
    }
}
