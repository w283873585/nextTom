package com.my.release04.connector.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Iterator;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import com.catalina.Connector;
import com.catalina.Context;
import com.catalina.Request;
import com.catalina.Response;
import com.catalina.Wrapper;

public class HttpRequestImpl implements Request{

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

}
