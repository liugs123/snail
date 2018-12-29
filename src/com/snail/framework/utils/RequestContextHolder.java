package com.snail.framework.utils;

import com.snail.framework.core.RequestContextAttribute;

public class RequestContextHolder {
	public static ThreadLocal<RequestContextAttribute> threadLocal = new ThreadLocal<RequestContextAttribute>();
	public static void putContext(RequestContextAttribute attribute){
		threadLocal.set(attribute);
	}
	public static RequestContextAttribute removeContext(){
		RequestContextAttribute attribute = threadLocal.get();
		threadLocal.remove();
		return attribute;
	}
	
}
