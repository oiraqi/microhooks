package io.microhooks.ddd.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Id;
import javax.persistence.PostPersist;

import org.springframework.beans.factory.annotation.Autowired;

import io.microhooks.ddd.EntityEvent;
import io.microhooks.eda.EventProducer;

public class SourceListener {

    @Autowired
    private EventProducer<Object, Object> eventProducer;
    
    @PostPersist
    public void onPostPersist(Object entity) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();
        Object key = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                Method[] methods = entity.getClass().getMethods();
                for (Method method : methods) {
                    String idGetterMethodNme = "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                    if (method.getName().equals(idGetterMethodNme) && method.getParameterCount() == 0) {
                        key = method.invoke(entity);
                        break;
                    }
                }
                break;
            }
        }
        EntityEvent<Object, Object> entityEvent = new EntityEvent<>(key, entity, EntityEvent.CREATED);
        eventProducer.publish(entityEvent, entity.getClass().getName());
    }
}
