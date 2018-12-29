package com.snail.framework.core;

import net.sf.cglib.proxy.Enhancer;

public class ProxyCreator {
	public static Object creatorProxy(Class<?> targeClass){
		Enhancer enhancer = new Enhancer();
		enhancer.setCallback(new EnhancerInterceptor(BeanManager.getInstance().getClassInterceptorMap().get(targeClass)));
		enhancer.setSuperclass(targeClass);
		return enhancer.create();
	}

	
	
}
