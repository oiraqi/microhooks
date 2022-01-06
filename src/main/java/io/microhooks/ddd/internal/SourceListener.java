package io.microhooks.ddd.internal;

import java.lang.reflect.Field;

import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.springframework.beans.factory.annotation.Autowired;

import io.microhooks.ddd.EntityEvent;
import io.microhooks.eda.EventProducer;
import io.microhooks.util.Reflector;
import io.microhooks.util.logging.Logged;

public class SourceListener {

    @Autowired
    private EventProducer<Object, Object> eventProducer;
    
    @PostPersist
    @Logged
    public void onPostPersist(Object entity) throws Exception {
        Object key = getKey(entity);
        EntityEvent<Object, Object> entityEvent = new EntityEvent<>(key, entity, EntityEvent.CREATED);
        eventProducer.publish(entityEvent, entity.getClass().getName());
    }

    @PostUpdate
    @Logged
    public void onPostUpdate(Object entity) throws Exception {
        Object key = getKey(entity);
        EntityEvent<Object, Object> entityEvent = new EntityEvent<>(key, entity, EntityEvent.UPDATED);
        eventProducer.publish(entityEvent, entity.getClass().getName());
    }

    @PostRemove
    @Logged
    public void onPostRemove(Object entity) throws Exception {
        Object key = getKey(entity);
        EntityEvent<Object, Object> entityEvent = new EntityEvent<>(key, null, EntityEvent.DELETED);
        eventProducer.publish(entityEvent, entity.getClass().getName());
    }

    private Object getKey(Object entity) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                return Reflector.getFieldValue(entity, field.getName());
            }
        }
        throw new IdNotFoundException();
    }
}
