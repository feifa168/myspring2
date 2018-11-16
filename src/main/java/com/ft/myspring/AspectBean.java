package com.ft.myspring;

@MyAspect
public class AspectBean {
    @MyBefore
    public void mybefore() {
        System.out.println("before");
    }

    @MyAfter
    public void myafter() {
        System.out.println("after");
    }
}
