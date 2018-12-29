package com.snail.framework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.snail.framework.annotation.Transaction;


public class TransactionInterceptor implements Interceptor {
	private static Logger logger = Logger.getLogger(TransactionInterceptor.class);
	
	ThreadLocal<Connection> threadLocal=ThreadLocalHolder.getThreadLocal();

	public Object invoke(InterceptChain chain) throws Throwable {
		Method method = chain.getMethod();
		Object result=null;
		Annotation annotation = method.getAnnotation(Transaction.class);
		Connection connection = threadLocal.get();
		if(connection!=null){
			chain.invoke();
		}else{
			try {
				if (annotation != null) {
					connection = BeanManager.getInstance().getFactory().getConnection();
					connection.setAutoCommit(false);
					logger.debug("open transaction...");
					threadLocal.set(connection);
					result = chain.invoke();
					connection.commit();
					logger.debug("commit transaction");
				}else{
					connection = BeanManager.getInstance().getFactory().getConnection();
					threadLocal.set(connection);
					result = chain.invoke();
				}
			}catch(Throwable e){
				if(annotation!=null){
					connection.rollback();
					logger.debug("roll back transaction");
				}
				throw e;
			} finally {
				threadLocal.remove();
				connection.close();
			}
		}
		return result;
	}

}
