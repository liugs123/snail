package com.snail.framework.core;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class RequestContextAttribute {
	private ServletRequest request;
	private ServletResponse response;
	public ServletRequest getRequest() {
		return request;
	}
	public RequestContextAttribute setRequest(ServletRequest request) {
		this.request = request;
		return this;
	}
	public ServletResponse getResponse() {
		return response;
	}
	public RequestContextAttribute setResponse(ServletResponse response) {
		this.response = response;
		return this;
	}
}
