package com.snail.framework.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.snail.framework.annotation.Aspectj;
import com.snail.framework.annotation.Component;
import com.snail.framework.annotation.Controller;
import com.snail.framework.annotation.Mapper;
import com.snail.framework.annotation.RequestMapping;
import com.snail.framework.annotation.Service;
import com.snail.framework.utils.AsmUtils;
import com.snail.framework.utils.FileUtils;
import com.snail.framework.utils.Stringutils;

public class BeanManager {
	private Map<Class<?>,Object> controllerMap=new ConcurrentHashMap<Class<?>,Object>();
	private Map<Class<?>,Object> serviceMap=new ConcurrentHashMap<Class<?>,Object>();
	private Map<Class<?>,Object> daoMap=new ConcurrentHashMap<Class<?>,Object>();

	private Map<Class<?>,Object> allMap=new ConcurrentHashMap<Class<?>,Object>();

	private Map<Method,List<String>> methodNameMap=new ConcurrentHashMap<Method,List<String>>();
	private Map<String,MethodAndObj> requestMap=new ConcurrentHashMap<String,MethodAndObj>();

	private List<Interceptor> interceptorList=new ArrayList<Interceptor>();
	
	private List<Class<?>> classList=new ArrayList<Class<?>>();
	
	private Map<Class<?>,List<Interceptor>> classInterceptorMap=new ConcurrentHashMap<>();

	private Properties config=null;

	private SqlConfiguration sqlConfiguration=new SqlConfiguration();

	private ConnectionFactory factory=new ConnectionFactory();

	private static BeanManager manager=new BeanManager();

	private void BeanManager(){}

	public static BeanManager getInstance(){
		return manager;
	}

	public void loadClasses() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String filePath = BeanManager.class.getResource("/").getFile();
		File file = new File(filePath);
		ArrayList<String> list = new ArrayList<String>();
		FileUtils.findClassFile(file, list);
		for (String classPath : list) {
			classPath = classPath.substring(classPath.indexOf("classes")).replace("classes"+File.separator,"").replace(File.separator, ".").replace(".class", "");
			Class<?> loadClass=null;
			try {
				loadClass = Class.forName(classPath, false, classLoader);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(loadClass.getAnnotation(Service.class)!=null
					||loadClass.getAnnotation(Controller.class)!=null
					||loadClass.getAnnotation(Mapper.class)!=null
					||loadClass.getAnnotation(Component.class)!=null
					||loadClass.getAnnotation(Aspectj.class)!=null){
				classList.add(loadClass);
			}
		}
	}

	public List<Class<?>> getClassList() {
		return classList;
	}

	public List<Interceptor> getInterceptorList() {
		return interceptorList;
	}

	public Map<Class<?>, List<Interceptor>> getClassInterceptorMap() {
		return classInterceptorMap;
	}

	public Map<Class<?>, Object> getAllMap() {
		return allMap;
	}

	public Properties getConfig() {
		return config;
	}

	public void setConfig(Properties config) {
		this.config = config;
	}

	public ConnectionFactory getFactory() {
		return factory;
	}

	public void setFactory(ConnectionFactory factory) {
		this.factory = factory;
	}

	public SqlConfiguration getSqlConfiguration() {
		return sqlConfiguration;
	}

    public Map<String, MethodAndObj> getRequestMap() {
        return requestMap;
    }

    public void initClazzBeanMap(){
		Set<Entry<Class<?>, Object>> entrySet = allMap.entrySet();
		for (Entry<Class<?>, Object> entry : entrySet) {
			Class<?> clazz = entry.getKey();
			if(clazz.getAnnotation(Controller.class)!=null){
				controllerMap.put(clazz, entry.getValue());
			}else if(clazz.getAnnotation(Service.class)!=null){
				serviceMap.put(clazz, entry.getValue());
			}else if(clazz.getAnnotation(Mapper.class)!=null){
				daoMap.put(clazz, entry.getValue());
			}
		}
	}
	
	public void initRequestMap(){
		Set<Entry<Class<?>, Object>> entrySet = controllerMap.entrySet();
		for (Entry<Class<?>, Object> entry : entrySet) {
			Class<?> clazz = entry.getKey();
			RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
			String typeValue =null;
			if(requestMapping!=null){
				typeValue = requestMapping.value();
			}
			if(typeValue!=null&&typeValue.length()>0){
				typeValue = Stringutils.addSeparator(typeValue);
			}else{
				typeValue="/";
			}
			Method[] declaredMethods = clazz.getDeclaredMethods();
			for (Method method : declaredMethods) {
				requestMapping = method.getAnnotation(RequestMapping.class);
				if(requestMapping!=null){
					String methodValue = requestMapping.value();
					String requestUri=typeValue+methodValue;
					requestUri=requestUri.replace("//", "/");
					MethodAndObj methodAndObj = new MethodAndObj();
					methodAndObj.setMethod(method);
					methodAndObj.setObj(entry.getValue());
					List<String> paramNameList = AsmUtils.getMethodParamNames(method);
					methodNameMap.put(method, paramNameList);
					requestMap.put(requestUri, methodAndObj);
				}
			}
		}
	}
}
