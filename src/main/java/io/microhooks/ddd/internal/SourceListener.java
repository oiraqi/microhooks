package io.microhooks.ddd.internal;

import java.lang.reflect.Field;

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
        Field[] fields = entity.getClass().getFields();
        Object key = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                key = field.get(entity);
            }
        }
        EntityEvent<Object, Object> entityEvent = new EntityEvent<>(key, entity, EntityEvent.CREATED);
        eventProducer.publish(entityEvent, entity.getClass().getName());
    }
}
