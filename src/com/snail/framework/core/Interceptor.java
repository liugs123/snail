package com.snail.framework.core;

public interface Interceptor {
	public Object invoke(InterceptChain chain) throws Throwable;
}
