package com.my.release05.startup;

import com.catalina.Loader;
import com.catalina.Pipeline;
import com.catalina.Valve;
import com.catalina.Wrapper;
import com.my.release04.connector.http.HttpConnector;
import com.my.release05.core.SimpleLoader;
import com.my.release05.core.SimpleWrapper;
import com.my.release05.valves.ClientIPLoggerValve;
import com.my.release05.valves.HeaderLoggerValve;

public final class Bootstrap1 {
  public static void main(String[] args) {

/* call by using http://localhost:8080/ModernServlet,
   but could be invoked by any name */

    HttpConnector connector = new HttpConnector();
    Wrapper wrapper = new SimpleWrapper();
    wrapper.setServletClass("PrimitiveServlet");
    Loader loader = new SimpleLoader();
    Valve valve1 = new HeaderLoggerValve();
    Valve valve2 = new ClientIPLoggerValve();

    wrapper.setLoader(loader);
    ((Pipeline) wrapper).addValve(valve1);
    ((Pipeline) wrapper).addValve(valve2);

    connector.setContainer(wrapper);

    try {
      connector.initialize();
      connector.start();

      // make the application wait until we press a key.
      System.in.read();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}