package com.ft;

import com.ft.myspring.AspectBean;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

class MyAOPMethod {
    public void before() {
        System.out.println("before");
    }
    public void after() {
        System.out.println("after");
    }
}

interface MyAnimal {
    void display();
    void say(String s, int i);
}

class MyDog implements MyAnimal{
    @Override
    public void display() {
        System.out.println("MyDog");
    }

    @Override
    public void say(String s, int i) {
        System.out.println(s + ", " + i);
    }
}

class MyCat implements MyAnimal{
    @Override
    public void display() {
        System.out.println("MyCat");
    }

    @Override
    public void say(String s, int i) {
        System.out.println(s + ", " + i);
    }
}

class MyStaticProxy implements MyAnimal {
    // 静态代理需要继承自接口，实际方法调用该对象方法
    private MyAnimal animal;
    private AspectBean aspect;

    public MyStaticProxy(MyAnimal animal, AspectBean aspect) {
        this.animal = animal;
        this.aspect = aspect;
    }

    @Override
    public void display() {
        if (null != aspect) {
            aspect.mybefore();
        }
        animal.display();
        if (null != aspect) {
            aspect.myafter();
        }
    }

    @Override
    public void say(String s, int i) {
        if (null != aspect) {
            aspect.mybefore();
        }
        animal.say(s, i);
        if (null != aspect) {
            aspect.myafter();
        }
    }
}

class MyInvocationHandler implements InvocationHandler {

    private Object subObj;
    private AspectBean aspect;

    public MyInvocationHandler(Object obj, AspectBean aspect) {
        this.subObj = obj;
        this.aspect = aspect;
    }

    public Object build() {
        return Proxy.newProxyInstance(subObj.getClass().getClassLoader(), subObj.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before method is " + method.getName() + ", params is " + Arrays.toString(args));
        if (null != aspect) {
            aspect.mybefore();
        }
        Object obj = method.invoke(subObj, args);
        if (null != aspect) {
            aspect.myafter();
        }
        System.out.println("after method is " + method.getName() + ", params is " + Arrays.toString(args));
        return obj;
    }
}

class ClassForCglib {
    public void display() {
        System.out.println("Cglib");
    }
    public void say(String s, int i) {
        System.out.println(s + ", " + i);
    }
}

class MyCglibProxy implements MethodInterceptor {
    private Enhancer enhancer = new Enhancer();
    private AspectBean aspect;

    @Override
    /**
     *
     * @param o 是被代理对象
     * @param method 调用方法的Method对象
     * @param args 方法参数
     * @param methodProxy
     * @return cglib生成用来代替Method对象的一个对象，使用MethodProxy比调用JDK自身的Method直接执行方法效率会有提升
     * @throws Throwable
     */
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("before " + methodProxy.getSuperName());
        System.out.println(method.getName() + ", params is " + Arrays.toString(args));
        if (null != aspect) {
            aspect.mybefore();
        }
        Object obj;
        obj = methodProxy.invokeSuper(o, args);
        if (null != aspect) {
            aspect.myafter();
        }
        // obj = methodProxy.invoke(o, args);   // 使用这种方式会发生死循环，因为方法会被拦截
        System.out.println("after " + methodProxy.getSuperName());
        return obj;
    }

    public Object newProxyInstance(Class<?> c, AspectBean aspect) {
        this.aspect = aspect;
        //设置产生的代理对象的父类。
        enhancer.setSuperclass(c);
        //设置CallBack接口的实例
        enhancer.setCallback(this);
        //使用默认无参数的构造函数创建目标对象
        return enhancer.create();
    }
}

public class TestProxy {
    @Test
    public void testStaticProxy() {
        System.out.println("============= testStaticProxy ============");
        MyStaticProxy staticProxy = new MyStaticProxy(new MyDog(), new AspectBean());
        staticProxy.display();
        staticProxy.say("hello", 3);
    }

    @Test
    public void testMyAnimal() {
        System.out.println("============= testMyAnimal ============");
        MyAnimal dog = new MyDog();
        MyInvocationHandler myinvok = new MyInvocationHandler(dog, new AspectBean());
        //InvocationHandler ih = Proxy.getInvocationHandler(myinvok);
        MyAnimal myDog;
        //myDog = (MyAnimal)Proxy.newProxyInstance(MyAnimal.class.getClassLoader(), new Class[]{MyAnimal.class}, myinvok);
        myDog = (MyAnimal)Proxy.newProxyInstance(MyAnimal.class.getClassLoader(), dog.getClass().getInterfaces(), myinvok);
        myDog.display();
        myDog.say("cat", 5);
    }
    @Test
    public void testMyAnimal2() {
        System.out.println("============= testMyAnimal2 ============");
        MyAnimal dog = new MyDog();
        // com.ft.MyDog, super java.lang.Object, interfaces [interface com.ft.MyAnimal]
        displayObjInfo(dog);

        MyInvocationHandler myinvok = new MyInvocationHandler(dog, new AspectBean());
        MyAnimal myDog = (MyAnimal)myinvok.build();

        // com.ft.$Proxy9, super java.lang.reflect.Proxy, interfaces [interface com.ft.MyAnimal]
        // // JDK代理类具体实现
        //public final class $Proxy0 extends Proxy implements MyAnimal
        //{
        //  ...
        //  public $Proxy0(InvocationHandler invocationhandler)
        //  {
        //    super(invocationhandler);
        //  }
        //  ...
        //  @Override
        //  public final String say(String str, int i){
        //    ...
        //    return super.h.invoke(this, m3, new Object[] {str, i});// 将方法调用转发给invocationhandler
        //    ...
        //  }
        //  ...
        //}
        displayObjInfo(myDog);

        myDog.display();
        myDog.say("bob", 2);
    }

    @Test
    public void testCglib() {
        System.out.println("============= testCglib ============");
        MyCglibProxy cglibProxy = new MyCglibProxy();
        ClassForCglib objCglig = (ClassForCglib) cglibProxy.newProxyInstance(ClassForCglib.class, new AspectBean());

        displayObjInfo(objCglig);

        // com.ft.ClassForCglib$$EnhancerByCGLIB$$eee76ee3, super com.ft.ClassForCglib, interfaces [interface net.sf.cglib.proxy.Factory]
        //before CGLIB$display$0
        //display, params is []
        //Cglib
        //after CGLIB$display$0
        //before CGLIB$say$1
        //say, params is [hello, 6]
        //hello, 6
        //after CGLIB$say$1
        objCglig.display();
        objCglig.say("hello", 6);
    }

    @Test
    public void testCglib2() {
        System.out.println("============= testCglib2 ============");
        MyCglibProxy cglibProxy = new MyCglibProxy();
        MyDog objCglig = (MyDog) cglibProxy.newProxyInstance(MyDog.class, new AspectBean());

        displayObjInfo(objCglig);

        objCglig.display();
        objCglig.say("hello", 6);
    }

    private void displayObjInfo(Object obj) {
        Class c = obj.getClass();
        System.out.println(c.getName()
                + ", super " + c.getSuperclass().getName()
                + ", interfaces " + Arrays.toString(c.getInterfaces()));
    }
}
