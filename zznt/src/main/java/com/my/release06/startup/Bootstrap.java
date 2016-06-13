package com.my.release06.startup;

import com.catalina.Connector;
import com.catalina.Context;
import com.catalina.Lifecycle;
import com.catalina.LifecycleListener;
import com.catalina.Loader;
import com.catalina.Mapper;
import com.catalina.Wrapper;
import com.my.release04.connector.http.HttpConnector;
import com.my.release06.core.SimpleContext;
import com.my.release06.core.SimpleContextLifecycleListener;
import com.my.release06.core.SimpleContextMapper;
import com.my.release06.core.SimpleLoader;
import com.my.release06.core.SimpleWrapper;

public final class Bootstrap {
  public static void main(String[] args) {
    Connector connector = new HttpConnector();
    Wrapper wrapper1 = new SimpleWrapper();
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("ModernServlet");

    Context context = new SimpleContext();
    context.addChild(wrapper1);
    context.addChild(wrapper2);

    Mapper mapper = new SimpleContextMapper();
    mapper.setProtocol("http");
    LifecycleListener listener = new SimpleContextLifecycleListener();
    ((Lifecycle) context).addLifecycleListener(listener);
    context.addMapper(mapper);
    Loader loader = new SimpleLoader();
    context.setLoader(loader);
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    connector.setContainer(context);
    try {
      connector.initialize();
      ((Lifecycle) connector).start();
      ((Lifecycle) context).start();

      // make the application wait until we press a key.
      System.in.read();
      ((Lifecycle) context).stop();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}