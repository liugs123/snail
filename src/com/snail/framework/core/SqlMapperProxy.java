package com.snail.framework.core;

import com.snail.framework.annotation.Param;
import com.snail.framework.utils.JdbcUtils;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlMapperProxy implements InvocationHandler {

	private static Logger logger = Logger.getLogger(SqlMapperProxy.class);

	private Class<?> targetClass=null;

	public SqlMapperProxy(Class<?> targetClass) {
		this.targetClass=targetClass;
	}

	private SqlConfiguration sqlConfiguration=BeanManager.getInstance().getSqlConfiguration();

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if("toString".equals(method.getName())){
			return null;
		}
		String canonicalName = targetClass.getSimpleName();
		String name=canonicalName+"."+method.getName();
		SqlTemplateParser parser = sqlConfiguration.getParser();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		String ognlSql=null;

		HashMap<String, Object> params = new HashMap<>();
		for (int i = 0; i < parameterTypes.length; i++) {
			Annotation[] parameterAnnotation = parameterAnnotations[i];
			if(parameterAnnotation.length>0&&parameterAnnotation[0].annotationType().equals(Param.class)){
				String value = ((Param) parameterAnnotation[0]).value();
				params.put(value,args[i]);
			}else{
				params.put("param"+i,args[i]);
			}

		}
		ognlSql = parser.getSql(name, params);

		ThreadLocal<Connection> threadLocal = ThreadLocalHolder.getThreadLocal();
		Connection connection = threadLocal.get();
		
		Map<String, SqlEntity> sqlEntityMap = sqlConfiguration.getSqlEntityMap();
		SqlEntity sqlEntity = sqlEntityMap.get(name);

		Object result=null;
		PreparedStatement preparedStatement=null;

		String prepareSql = JdbcUtils.getPrepareSql(ognlSql);
		List<Object> paramList = JdbcUtils.getParamList(ognlSql, params);

		preparedStatement = connection.prepareStatement(prepareSql);
		logger.debug(prepareSql);
		logger.debug(paramList);
		JdbcUtils.setParams(preparedStatement,paramList);

		if("SELECT".equals(sqlEntity.getType())){
			ResultSet resultSet = preparedStatement.executeQuery();
			result = JdbcUtils.getResultEntity(resultSet,method);
		} else {
			result = preparedStatement.executeUpdate();
		}

		return result;
	}
	
	public Object getProxy() {  
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),   
                new Class<?>[]{targetClass}, this);  
    }  
	
}
