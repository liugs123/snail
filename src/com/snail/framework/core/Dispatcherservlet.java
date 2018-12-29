package com.snail.framework.core;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.snail.framework.annotation.Param;
import com.snail.framework.annotation.RequestBody;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.snail.framework.annotation.ResponseBody;
import com.snail.framework.utils.CommonUtils;
import com.snail.framework.utils.ReflectUtils;
import com.snail.framework.utils.RequestContextHolder;
import com.snail.framework.utils.ResourceUtils;

public class Dispatcherservlet extends HttpServlet  {
	
	private static final long serialVersionUID = 1630820858451585858L;
	
	private static Logger logger = Logger.getLogger(Dispatcherservlet.class);
	
	public static Map<String,MethodAndObj> requestMap=BeanManager.getInstance().getRequestMap();
	
	private String prefix;
	private String suffix;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		prefix=ResourceUtils.getString(BeanManager.getInstance().getConfig(), "view.prefix");
		suffix=ResourceUtils.getString(BeanManager.getInstance().getConfig(), "view.suffix");
		super.init(config);
	}

	@Override
	public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
		RequestContextHolder.putContext(new RequestContextAttribute().setRequest(request).setResponse(response));
		HttpServletRequest req=(HttpServletRequest) request;
		logger.debug(req.getRequestURI());
		String contextPath = req.getContextPath();
		String requestURI = req.getRequestURI();
		String uri = requestURI.replace(contextPath, "");
		MethodAndObj methodAndObj = requestMap.get(uri);
		if(methodAndObj==null){
			logger.warn("未查找到此路径");
			return;
		}
		Method method = methodAndObj.getMethod();
		Object obj = methodAndObj.getObj();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		try {
			Object[] params=null;
			if(parameterTypes.length>0){
				params=new Object[parameterTypes.length];
				for (int i = 0; i < parameterTypes.length; i++) {
					if(parameterTypes[i]==HttpServletRequest.class){
						params[i]=request;
						continue;
					}else if(parameterTypes[i]==HttpServletResponse.class){
						params[i]=response;
						continue;
					}
					Annotation[] paramAnnotation = parameterAnnotations[i];
					for (Annotation annotation : paramAnnotation) {
						if(annotation.annotationType().equals(Param.class)){
							String parameter = req.getParameter(((Param) annotation).value());
							params[i]=CommonUtils.convertParamTypeByClass(parameter,parameterTypes[i]);
							continue;
						}else if(annotation.annotationType().equals(RequestBody.class)){
							String body = CommonUtils.readRequestBody(req);
							Object parseObject = JSONObject.parseObject(body, parameterTypes[i]);
							params[i]=parseObject;
							continue;
						}
					}
				}
			}
			/*List<String> list = BeanCache.methodNameMap.get(method);
			if(list.size()>0){
				params=new Object[list.size()];
				for (int i = 0; i < list.size(); i++) {
					if(parameterTypes[i]==HttpServletRequest.class){
						params[i]=request;
						continue;
					}else if(parameterTypes[i]==HttpServletResponse.class){
						params[i]=response;
						continue;
					}
					String param = req.getParameter(list.get(i));
					if(param!=null){
						params[i]=CommonUtils.convertParamTypeByClass(param, parameterTypes[i]);
					}else{
						params[i]=null;
					}
				}
				
				Map<Class<?>, Object> paramObj = new HashMap<Class<?>,Object>();
				
				Enumeration<String> parameterNames = request.getParameterNames();
				
				while(parameterNames.hasMoreElements()){
					String paramName = parameterNames.nextElement();
					String paramValue = request.getParameter(paramName);
					logger.debug(paramName+":"+paramValue);  
					for (int i = 0; i < parameterTypes.length; i++) {
						Object param = CommonUtils.getObjectByClassAndParam(parameterTypes[i], paramValue, paramName,paramObj);
						if(param!=null){
							params[i]=param;
							paramObj.put(parameterTypes[i], param);
						}
					}
				}
			}*/
			
			
			Object result = ReflectUtils.invokeMethod(method, obj, params);
			
			if(method.getReturnType()==void.class){
				return;
			}else if(method.getAnnotation(ResponseBody.class)!=null){
				String jsonStr = JSONObject.toJSONString(result);
				response.setContentType("text/html;charset=UTF-8");
			    response.setCharacterEncoding("UTF-8");
				PrintWriter writer = response.getWriter();
				logger.debug("return json:"+jsonStr);
				writer.append(jsonStr);
			}else{
				/*OutputStream outputStream=null;
				InputStream inputStream=null;
				try {
					outputStream = new BufferedOutputStream(response.getOutputStream());
					String realPath = this.getServletContext().getRealPath("")+"/";
					inputStream =new BufferedInputStream(new FileInputStream(realPath+prefix+(String)result+suffix));
					byte[] box=new byte[1024];
					int len=0;
					while((len=inputStream.read(box))!=-1){
						outputStream.write(box,0,len);
						outputStream.flush();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(inputStream!=null){
						inputStream.close();
					}
				}*/

				request.getRequestDispatcher(prefix+(String)result+suffix).forward(request, response);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
}
