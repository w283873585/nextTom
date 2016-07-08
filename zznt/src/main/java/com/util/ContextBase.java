package com.util;

import com.catalina.Realm;
import com.catalina.deploy.LoginConfig;
import com.catalina.deploy.SecurityConstraint;

public abstract class ContextBase {
	private Realm realm = null;
	
	public LoginConfig getLoginConfig() {
		return null;
	}
	
	public Realm getRealm() {
		return realm;
	}


    /**
     * Set the Realm with which this Container is associated.
     *
     * @param realm The newly associated Realm
     */
    public void setRealm(Realm realm) {
    	this.realm = realm;
    }
    
    public SecurityConstraint[] findConstraints() {
    	return null;
    }
    
    public void setConfigured(boolean b) {}


	public void setLoginConfig(LoginConfig loginConfig){}
	
	public void setPath(String path) {}

	public void setDocBase(String doc) {}
	
	public void addConstraint(SecurityConstraint constraint) {}
}
