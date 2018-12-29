package com.snail.framework.utils;

import com.snail.framework.annotation.ReturnType;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.beanutils.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdbcUtils {

    public static String getType(String sql){
        if(sql.startsWith("update")||sql.startsWith("UPDATE")){
            return "UPDATE";
        }
        if(sql.startsWith("insert")||sql.startsWith("INSERT")){
            return "INSERT";
        }
        if(sql.startsWith("select")||sql.startsWith("SELECT")){
            return "SELECT";
        }
        if(sql.startsWith("delete")||sql.startsWith("DELETE")){
            return "DELETE";
        }
        return null;
    }

    public static Object getResultEntity(ResultSet resultSet, Method method) throws InvocationTargetException, IllegalAccessException {
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            Map<String, Object> map =null;
            int col = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                map = new HashMap<>();
                for (int i = 1; i <= col; i++) {
                    String columnName = resultSet.getMetaData().getColumnLabel(i);
                    Object value = resultSet.getObject(i);
                    map.put(CommonUtils.formatName(columnName),value);
                }
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(list.size()==0){
            return null;
        }

        ReturnType annotation = method.getAnnotation(ReturnType.class);
        if(annotation==null){
            return list;
        }
        Object object=null;
        Class<?> type = annotation.value();
        Class<?> returnType = method.getReturnType();
        if(List.class.isAssignableFrom(returnType)){
            List resultList = new ArrayList();
            if(CommonUtils.isBasisClass(type)){
                for (Map<String, Object> map : list) {
                    Set<Map.Entry<String, Object>> entries = map.entrySet();
                    for (Map.Entry<String, Object> entry : entries) {
                        Object value = entry.getValue();
                        object = CommonUtils.convertParamTypeByClass(value.toString(), type);
                        resultList.add(object);
                    }
                }
                return resultList;
            }

            for (Map<String, Object> map : list) {
                object = ReflectUtils.newInstance(type);
                BeanUtils.copyProperties(object,map);
                resultList.add(object);
            }
            return resultList;
        }else{
            if(CommonUtils.isBasisClass(type)){
                Map<String,Object> map=list.get(0);
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    Object value = entry.getValue();
                    object = CommonUtils.convertParamTypeByClass(value.toString(), type);
                    return object;
                }
            }
            object = ReflectUtils.newInstance(type);
            BeanUtils.copyProperties(object,list.get(0));
        }
        return object;
    }
    public static String getPrepareSql(String ognlSql){
        return ognlSql.replaceAll("@\\{[0-9A-Za-z\\.\\[\\]]+\\}", "?");
    }

    public static List<Object> getParamList(String ognlSql,Map<String,Object> params){
        ArrayList<Object> paramList = new ArrayList<>();

        Pattern pattern = Pattern.compile("@\\{[0-9A-Za-z\\.\\[\\]]+\\}");
        Matcher matcher = pattern.matcher(ognlSql);

        OgnlContext context = new OgnlContext();
        context.setValues(params);
        String ognlExpress=null;
        Object value=null;
        while (matcher.find()) {
            ognlExpress = "#"+matcher.group().replaceAll("\\{|\\}|\\@","");
            try {
                value = Ognl.getValue(ognlExpress, context, context.getRoot());
                paramList.add(value);
            } catch (OgnlException e) {
                throw new RuntimeException("ognl 解析失败");
            }
        }
        return paramList;
    }

    public static void setParams(PreparedStatement preparedStatement, List<Object> paramList) {
        try {
            Object value=null;
            for (int i = 1; i <paramList.size()+1 ; i++) {
                value = paramList.get(i-1);
                if(value instanceof String){
                    preparedStatement.setString(i, (String)value);
                }else if(value instanceof Integer){
                    preparedStatement.setInt(i, (Integer) value);
                }else if(value instanceof Double){
                    preparedStatement.setDouble(i, (Double) value);
                }else if(value instanceof Boolean){
                    preparedStatement.setBoolean(i, (Boolean) value);
                }else if(value instanceof Date){
                    preparedStatement.setDate(i, new java.sql.Date(((Date)value).getTime()));
                }else{
                    preparedStatement.setObject(i,null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
