package io.microhooks.internal.util;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import org.atteo.classindex.ClassIndex;

import io.microhooks.consumer.Sink;

public class ClassScanner {

    public static void test(EntityManager em) {
        Iterable<Class<?>> sinks = ClassIndex.getAnnotated(Sink.class);
    
        for (Class<?> sink : sinks) {
            try {
                Object o = sink.getDeclaredConstructor().newInstance();
                Method m = sink.getDeclaredMethod("setName", String.class);
                m.invoke(o, "Fadi");
                System.out.println(o);
                em.persist(o);

            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }

}
