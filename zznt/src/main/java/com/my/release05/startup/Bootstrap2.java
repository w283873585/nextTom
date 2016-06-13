package com.my.release05.startup;

import com.catalina.Context;
import com.catalina.Loader;
import com.catalina.Mapper;
import com.catalina.Pipeline;
import com.catalina.Valve;
import com.catalina.Wrapper;
import com.my.release04.connector.http.HttpConnector;
import com.my.release05.core.SimpleContext;
import com.my.release05.core.SimpleContextMapper;
import com.my.release05.core.SimpleLoader;
import com.my.release05.core.SimpleWrapper;
import com.my.release05.valves.ClientIPLoggerValve;
import com.my.release05.valves.HeaderLoggerValve;

public final class Bootstrap2 {
  public static void main(String[] args) {
    HttpConnector connector = new HttpConnector();
    
    Wrapper wrapper1 = new SimpleWrapper();
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("ModernServlet");

    Context context = new SimpleContext();
    context.addChild(wrapper1);
    context.addChild(wrapper2);

    Valve valve1 = new HeaderLoggerValve();
    Valve valve2 = new ClientIPLoggerValve();

    ((Pipeline) context).addValve(valve1);
    ((Pipeline) context).addValve(valve2);

    Mapper mapper = new SimpleContextMapper();
    mapper.setProtocol("http");
    context.addMapper(mapper);
    Loader loader = new SimpleLoader();
    context.setLoader(loader);
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    connector.setContainer(context);
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