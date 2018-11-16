package com.ft.bean;

public class UserBean {
    private String name;
    private int age;

    public String getName() {
        System.out.println(toString());
        return name;
    }

    public void setName(String name) {
        System.out.println(toString());
        this.name = name;
    }

    public int getAge() {
        System.out.println(toString());
        return age;
    }

    public void setAge(int age) {
        System.out.println(toString());
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
