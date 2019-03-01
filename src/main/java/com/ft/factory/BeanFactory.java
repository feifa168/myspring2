package com.ft.factory;

public interface BeanFactory {
    Object getBean(String id, Class<?> className);
}
