package com.ft.bean;

import com.ft.myannotation.MyCreator;
import com.ft.myannotation.MyParam;

public class Cat implements Animal {

    private String name;
    private int age;

    @MyCreator
    public Cat(@MyParam(localname = "name") String name, @MyParam(localname = "age") int age) {
        super();
        this.name = name;
        this.age  = age;
    }

    @Override
    public String getName() {
        return "Cat "+name + ", age is " + age;
    }
}
