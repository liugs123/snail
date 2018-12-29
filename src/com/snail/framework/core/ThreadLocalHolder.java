package com.snail.framework.core;

import java.sql.Connection;

public class ThreadLocalHolder {
	private static ThreadLocal<Connection> threadLocal=new ThreadLocal<Connection>();
 
	public static ThreadLocal<Connection> getThreadLocal(){
		return threadLocal;
	}
}
