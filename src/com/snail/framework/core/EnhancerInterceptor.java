package com.snail.framework.core;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class EnhancerInterceptor implements MethodInterceptor {
	
	private List<Interceptor> list=null;

	public EnhancerInterceptor(List<Interceptor> list) {
		this.list=list;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy methodProxy) throws Throwable {
		InterceptChain chain = new InterceptChain();
		chain.setInterceptorList(list);
		chain.setFields(methodProxy, args, method,obj);
		return chain.invoke();
	}

}
