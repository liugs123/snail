package com.snail.framework.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.MethodProxy;

import com.snail.framework.annotation.Aspectj;
import com.snail.framework.annotation.Controller;
import com.snail.framework.annotation.Service;

public class InterceptChain{
	
	private List<Interceptor> InterceptorList;
	
	private int size=0;
	
	private Object obj;
	
	MethodProxy methodProxy;
	
	Method method;
	
	Object[] args;
	
	public List<Interceptor> getInterceptorList() {
		return InterceptorList;
	}

	public void setInterceptorList(List<Interceptor> interceptorList) {
		InterceptorList = interceptorList;
	}

	public void setFields(MethodProxy methodProxy,Object[] args,Method method,Object obj){
		this.args=args;
		this.method=method;
		this.methodProxy=methodProxy;
		this.obj=obj;
	}
	
	public MethodProxy getMethodProxy() {
		return methodProxy;
	}

	public void setMethodProxy(MethodProxy methodProxy) {
		this.methodProxy = methodProxy;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object invoke() throws Throwable{
		Object result=null;
		if(size==InterceptorList.size()){
			result=methodProxy.invokeSuper(obj, args);
		}else{
			result = InterceptorList.get(size++).invoke(this);
		}
		return result;
	}
	
	public static List<Interceptor> findCandidateInterceptor(Class<?> clazz){
		List<Interceptor> list = new ArrayList<Interceptor>();
		if(clazz.getAnnotation(Service.class)!=null){
			TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
			list.add(transactionInterceptor);
		}
		for (Interceptor interceptor : BeanManager.getInstance().getInterceptorList()) {
			Aspectj aspectj = interceptor.getClass().getAnnotation(Aspectj.class);
			String value = aspectj.value();
			if(clazz.getAnnotation(Service.class)!=null){
				if("service".equalsIgnoreCase(value)){
					list.add(interceptor);
				}
			}else if(clazz.getAnnotation(Controller.class)!=null){
				if("controller".equalsIgnoreCase(value)){
					list.add(interceptor);
				}
			}
		}
		return list;
	}

	
}
