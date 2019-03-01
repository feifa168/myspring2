package com.ft.factory;

import com.ft.myannotation.MyCreator;
import com.ft.myannotation.MyParam;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApplicationContext implements BeanFactory {
    public MyApplicationContext(String beanXml) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document doc = null;
        //doc = reader.read(this.getClass().getClassLoader().getResourceAsStream(beanXml));
        doc = reader.read(new File(beanXml));

        Element root = doc.getRootElement();
        List<Element> elements = root.elements();
        for (Element element : elements) {
            String attId = element.attributeValue("id");
            String attClass = element.attributeValue("class");
            if ((null != attId) && (null != attClass)) {
                try {
                    Constructor[] cs = Class.forName(attClass).getConstructors();
                    boolean find = false;
                    Object myobj = null;
                    for (Constructor c : cs) {
                        Annotation[] annos = c.getAnnotations();
                        for (Annotation anno : annos) {
                            if (anno instanceof MyCreator) {
                                Annotation[][] annos2 = c.getParameterAnnotations();

                                List<String> params = new ArrayList<>();
                                Map<String, Object> mapParams = new HashMap<>();
                                mapParams.put("name", "tom");
                                mapParams.put("age", 20);

                                for (Annotation[] aout : annos2) {
                                    for (Annotation ainner : aout) {
                                        if (ainner instanceof MyParam) {
                                            params.add(((MyParam) ainner).localname());
                                            break;
                                        }
                                    }
                                }

                                Object[] objs = new Object[params.size()];
                                int idx = 0;
                                for (String param : params) {
                                    objs[idx++] = mapParams.get(param);
                                }
                                try {
                                    myobj = c.newInstance(objs);
                                    find = true;
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                    if (!find) {
                        try {
                            myobj = Class.forName(attClass).newInstance();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    mapBeans.put(attId, myobj);
                    //mapBeans.put(attId, );
                }  catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object getBean(String id, Class<?> className) {
        Object obj = mapBeans.get(id);
//        if (obj instanceof className) {
//        }
        return obj;
    }

    private Map<String, Object> mapBeans = new HashMap<>();
}
