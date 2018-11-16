package com.ft.myspring;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyRegister {
    private String beanName;
    private Class<?> beanCls;
    private Object beanObj;
    private String methodName;
    private String[] paramTypes;
    private Class[] beanParamCls;
    private Object[] paramValues;
    private HashMap<String, Object> params;

    private String aspectName;
    private Method beforeMethod;
    private Object aspectObj;
    private Method afterMethod;

    public MyRegister() {
        String beanName = null;
        Class<?> beanCls = null;
        Object beanObj = null;
        String methodName = null;
        String[] paramTypes = null;
        Class[] beanParamCls = null;
        Object[] paramValues = null;
        Map<String, Object> params = null;

        String aspectName = null;
        Method beforeMethod = null;
        Object aspectObj = null;
        Method afterMethod = null;
    }
    public void init(String beanName, String methodName, HashMap<String, Object> params, String aspectName) {
        this.beanName = beanName;
        this.methodName = methodName;
        this.params = params;
        this.aspectName = aspectName;
    }

    public void init(Object beanObj, String methodName, HashMap<String, Object> params, String aspectName) {
        this.beanObj = beanObj;
        this.beanCls = beanObj.getClass();
        this.beanName = beanCls.getName();
        this.methodName = methodName;
        this.params = params;
        this.aspectName = aspectName;
    }

    private void handleAspect() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Class<?> aspectCls = null;
        if (aspectName != null) {
            aspectCls = Class.forName(aspectName);
            aspectObj = aspectCls.newInstance();
            Method[] mds = aspectCls.getDeclaredMethods();
            for (Method md : mds) {
                Annotation[] ans = md.getDeclaredAnnotations();
                for (Annotation an : ans) {
                    if (an instanceof MyBefore) {
                        beforeMethod = md;
                        break;
                    } else if (an instanceof MyAfter) {
                        afterMethod = md;
                        break;
                    }
                }
            }
        }
    }

    private void handleBean() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (beanCls == null) {
            beanCls = Class.forName(beanName);
            beanObj = beanCls.newInstance();
        }

        int paramNum = 0;
        if (params != null && params.size() > 0)
            paramNum = params.size();
        if (paramNum > 0) {
            beanParamCls = new Class[paramNum];
            paramValues  = new Object[paramNum];
            int i=0;
            for (Map.Entry<String, Object> kv : params.entrySet()) {
                beanParamCls[i] = Class.forName(kv.getKey());
                paramValues[i] = kv.getValue();
            }
        }
    }

    public void handle() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        handleBean();
        handleAspect();

        if (beforeMethod != null) {
            beforeMethod.invoke(aspectObj);
        }

        Method md;
        int paramNum = 0;
        if (params != null && params.size() > 0)
            paramNum = paramValues.length;
        if (paramNum > 0) {
            md = beanCls.getMethod(methodName, beanParamCls);
            md.invoke(beanObj, paramValues);
        } else {
            md = beanCls.getMethod(methodName);
            md.invoke(beanObj);
        }

        if (afterMethod != null) {
            afterMethod.invoke(aspectObj);
        }
    }
}
