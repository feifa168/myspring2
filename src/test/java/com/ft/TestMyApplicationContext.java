package com.ft;

import com.ft.bean.Animal;
import com.ft.bean.Cat;
import com.ft.factory.MyApplicationContext;
import com.ft.myannotation.MyCreator;
import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

public class TestMyApplicationContext {
    @Test
    public void testMyApp() throws DocumentException {
        MyApplicationContext myapp = new MyApplicationContext("bean.xml");
        Cat cat = (Cat) myapp.getBean("cat", Animal.class);
        System.out.println(cat.getName());
    }
    @Test
    public void testAnno () throws ClassNotFoundException {
        Constructor[] cons = Class.forName("com.ft.bean.Cat").getConstructors();
        for (Constructor con : cons) {
            Annotation[] annos = con.getAnnotations();
            for (Annotation anno : annos) {
                if (anno instanceof MyCreator) {
                    System.out.println("yes");
                } else {
                    System.out.println("not");
                }
            }
        }
    }
}
