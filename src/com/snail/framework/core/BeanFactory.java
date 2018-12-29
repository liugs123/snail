package com.snail.framework.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.snail.framework.annotation.Aspectj;
import com.snail.framework.annotation.Autowired;
import com.snail.framework.annotation.Mapper;
import com.snail.framework.annotation.Value;
import com.snail.framework.utils.CommonUtils;
import com.snail.framework.utils.ReflectUtils;
import com.snail.framework.utils.ResourceUtils;

public class BeanFactory {
	private static Logger logger = Logger.getLogger(BeanFactory.class);

	private BeanManager beanManager;

	public void init (){
		this.beanManager=BeanManager.getInstance();
		this.beanManager.setConfig(ResourceUtils.loadProps("application.properties"));
		this.beanManager.getFactory().init(this.beanManager.getConfig());
		this.beanManager.loadClasses();
		this.createInterceptor();
		this.create();
		this.beanManager.initClazzBeanMap();
		this.beanManager.initRequestMap();
		this.beanManager.getSqlConfiguration().init();
	}
	
	public void createInterceptor(){
		for (Class<?> clazz : beanManager.getClassList()) {
			Object newInstance=null;
			if(clazz.getAnnotation(Aspectj.class)!=null){
				newInstance = ReflectUtils.newInstance(clazz);
				beanManager.getInterceptorList().add((Interceptor) newInstance);
				logger.debug("interceptor:"+clazz.getName());
				continue;
			}
		}
		logger.debug("interceptor number:"+beanManager.getInterceptorList().size());
	}
	
	public void create(){
		for (Class<?> clazz : beanManager.getClassList()) {
			 this.getBean(clazz);
		}
	}
	
	public Object doCreate(Class<?> clazz){
		Object obj=null;
		if(clazz.getAnnotation(Mapper.class)!=null){
			obj = new SqlMapperProxy(clazz).getProxy();
		}else{
			List<Interceptor> interceptorList = InterceptChain.findCandidateInterceptor(clazz);
			if(interceptorList.size()>0){
				beanManager.getClassInterceptorMap().put(clazz, interceptorList);
				obj = ProxyCreator.creatorProxy(clazz);
			}else{
				obj = ReflectUtils.newInstance(clazz);
			}
		}
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Value valueAnn = field.getAnnotation(Value.class);
			if(valueAnn!=null){
				String value = valueAnn.value();
				String fieldValue = ResourceUtils.getString(beanManager.getConfig(), value);
				ReflectUtils.setField(field, CommonUtils.convertParamTypeByClass(fieldValue, field.getType()), obj);
				continue;
			}
			Autowired annotation = field.getAnnotation(Autowired.class);
			if(annotation!=null){
				Class<?> fieldType = field.getType();
				if(beanManager.getClassList().contains(fieldType)){
					Object fieldObj = this.getBean(fieldType);
					ReflectUtils.setField(field, fieldObj, obj);
				}
				
			}
			
		}
		
		logger.debug("create object:"+clazz.getName());
		return obj;
	}
	
	
	public Object getBean(Class<?> clazz){
		Object object = beanManager.getAllMap().get(clazz);
		if(object==null){
			object = this.doCreate(clazz);
			beanManager.getAllMap().put(clazz, object);
		}
		return object;
	}
	
}
