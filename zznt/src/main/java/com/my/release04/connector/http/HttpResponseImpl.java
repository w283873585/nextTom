package com.my.release04.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import com.catalina.Connector;
import com.catalina.Context;
import com.catalina.Request;
import com.catalina.Response;

public class HttpResponseImpl implements Response{

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
	public int getContentCount() {
		// TODO Auto-generated method stub
		return 0;
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
	public void setAppCommitted(boolean appCommitted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAppCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getIncluded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIncluded(boolean included) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Request getRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRequest(Request request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServletResponse getResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStream(OutputStream stream) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSuspended(boolean suspended) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSuspended() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ServletOutputStream createOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void finishResponse() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getReporter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recycle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendAcknowledgement() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
