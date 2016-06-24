package com.my.release10.startup;

import com.catalina.Connector;
import com.catalina.Context;
import com.catalina.Lifecycle;
import com.catalina.LifecycleListener;
import com.catalina.Loader;
import com.catalina.Realm;
import com.catalina.Wrapper;
import com.catalina.deploy.LoginConfig;
import com.catalina.deploy.SecurityCollection;
import com.catalina.deploy.SecurityConstraint;
import com.catalina.loader.WebappLoader;
import com.my.release04.connector.http.HttpConnector;
import com.my.release07.core.SimpleContext;
import com.my.release10.core.SimpleContextConfig;
import com.my.release10.core.SimpleWrapper;
import com.my.release10.realm.SimpleRealm;

public final class Bootstrap1 {
  public static void main(String[] args) {

  //invoke: http://localhost:8080/Modern or  http://localhost:8080/Primitive

    System.setProperty("catalina.base", System.getProperty("user.dir"));
    Connector connector = new HttpConnector();
    Wrapper wrapper1 = new SimpleWrapper();
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("ModernServlet");

    Context context = new SimpleContext();
    // StandardContext's start method adds a default mapper
    context.setPath("/myApp");
    context.setDocBase("myApp");
    LifecycleListener listener = new SimpleContextConfig();
    ((Lifecycle) context).addLifecycleListener(listener);

    context.addChild(wrapper1);
    context.addChild(wrapper2);
    // for simplicity, we don't add a valve, but you can add
    // valves to context or wrapper just as you did in Chapter 6

    Loader loader = new WebappLoader();
    context.setLoader(loader);
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    // add ContextConfig. This listener is important because it configures
    // StandardContext (sets configured to true), otherwise StandardContext
    // won't start

    // add constraint
    SecurityCollection securityCollection = new SecurityCollection();
    securityCollection.addPattern("/");
    securityCollection.addMethod("GET");

    SecurityConstraint constraint = new SecurityConstraint();
    constraint.addCollection(securityCollection);
    constraint.addAuthRole("manager");
    LoginConfig loginConfig = new LoginConfig();
    loginConfig.setRealmName("Simple Realm");
    // add realm
    Realm realm = new SimpleRealm();

    context.setRealm(realm);
    context.addConstraint(constraint);
    context.setLoginConfig(loginConfig);

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