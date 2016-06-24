package com.util;

import com.catalina.Realm;
import com.catalina.deploy.LoginConfig;
import com.catalina.deploy.SecurityConstraint;

public abstract class ContextBase {
	public LoginConfig getLoginConfig() {
		return null;
	}
	
	public Realm getRealm() {
		return null;
	}


    /**
     * Set the Realm with which this Container is associated.
     *
     * @param realm The newly associated Realm
     */
    public void setRealm(Realm realm) {
    	
    }
    
    public SecurityConstraint[] findConstraints() {
    	return null;
    }
    
    public void setConfigured(boolean b) {}


	public void setLoginConfig(LoginConfig loginConfig){}
}
