package com.ft;

import com.ft.myspring.MyRegister;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TestUserBean {
//    before
//    UserBean{name='null', age=0}
//    after
//    before
//    UserBean{name='tom', age=0}
//    after
    @Test
    public void testBean() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> cls = Class.forName("com.ft.myspring.MyRegister");
        Object obj = cls.newInstance();
        Map<String, Object> mso = new HashMap<>();
        mso.put("java.lang.String", "tom");
//        Class[] parCls = new Class[]{String.class, String.class, mso.getClass(), String.class};
//        Object[] parObj = new Object[]{"com.ft.bean.UserBean", "setName", mso, "com.ft.myspring.AspectBean"};
        Class[] parCls = new Class[]{Object.class, String.class, mso.getClass(), String.class};
        Class<?> beanCls = Class.forName("com.ft.bean.UserBean");
        Object beanObj = beanCls.newInstance();
        Object[] parObj = new Object[]{beanObj, "setName", mso, "com.ft.myspring.AspectBean"};

        Method m = cls.getMethod("init", parCls);
        m.invoke(obj, parObj);

        m = cls.getMethod("handle");
        m.invoke(obj);

        parObj[1] = "getName";
        parObj[2] = null;
        m = cls.getMethod("init", parCls);
        m.invoke(obj, parObj);
        m = cls.getMethod("handle");
        m.invoke(obj);
    }
}
