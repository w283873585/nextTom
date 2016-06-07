package com.my.release04.connector.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;

import com.catalina.Connector;
import com.catalina.Context;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Wrapper;

public class HttpRequestImpl implements HttpRequest{

	@Override
	public String getAuthorization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthorization(String authorization) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Connector getConnector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConnector(Connector connector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContext(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletRequest getRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response getResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResponse(Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Socket getSocket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSocket(Socket socket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStream(InputStream stream) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Wrapper getWrapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWrapper(Wrapper wrapper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServletInputStream createInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void finishRequest() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getNote(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getNoteNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recycle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeNote(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentLength(int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentType(String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNote(String name, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProtocol(String protocol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRemoteAddr(String remote) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScheme(String scheme) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSecure(boolean secure) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setServerName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setServerPort(int port) {
		// TODO Auto-generated method stub
		
	}

	public void setQueryString(String substring) {
		// TODO Auto-generated method stub
		
	}

	public void setRequestedSessionId(String substring) {
		// TODO Auto-generated method stub
		
	}

	public void setRequestedSessionURL(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCookie(Cookie cookie) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addHeader(String name, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLocale(Locale locale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addParameter(String name, String[] values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearCookies() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearHeaders() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearLocales() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearParameters() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAuthType(String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContextPath(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMethod(String method) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPathInfo(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRequestedSessionCookie(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRequestURI(String uri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDecodedRequestURI(String uri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDecodedRequestURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServletPath(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUserPrincipal(Principal principal) {
		// TODO Auto-generated method stub
		
	}

	public void setInet(InetAddress inetAddress) {
		// TODO Auto-generated method stub
		
	}

	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

}
