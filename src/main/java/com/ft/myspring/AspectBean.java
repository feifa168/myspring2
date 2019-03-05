package com.ft.myspring;

@MyAspect
public class AspectBean {
    @MyBefore
    public void mybefore() {
        System.out.println("custom before");
    }

    @MyAfter
    public void myafter() {
        System.out.println("custom after");
    }
}
