package com.my.release04.startup;

import com.catalina.Container;
import com.my.release04.connector.http.HttpConnector;
import com.my.release04.core.SimpleContainer;

public class Bootstrap {
	public static void main(String[] args) throws Exception {
		HttpConnector connector = new HttpConnector();
		Container container = new SimpleContainer();
	    connector.setContainer(container);
	    try {
	      connector.initialize();
	      connector.start();

	      // make the application wait until we press any key.
	      System.in.read();
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	}
}
