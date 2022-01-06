package io.microhooks.ddd.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.springframework.beans.factory.annotation.Autowired;

import io.microhooks.ddd.OnCreate;
import io.microhooks.ddd.OnDelete;
import io.microhooks.ddd.OnUpdate;
import io.microhooks.ddd.Track;
import io.microhooks.eda.EventProducer;
import io.microhooks.eda.MappedEvent;
import io.microhooks.util.Reflector;
import io.microhooks.util.logging.Logged;

public class CustomListener {
    @Autowired
    private EventProducer<Object, Object> eventProducer;

    @PostPersist
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostPersist(Object entity) throws Exception {
        setTrackedFields(entity);
        for (Method method : entity.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnCreate.class)) {
                List<MappedEvent<Object, Object>> mappedEvents = (List<MappedEvent<Object, Object>>) method.invoke(entity);
                publish(mappedEvents);
                return;
            }
        }
    }

    @PostUpdate
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostUpdate(Object entity) throws Exception {
        for (Method method : entity.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnUpdate.class)) {
                Map<String, Object> trackedFields = ((Trackable)entity).getTrackedFields();
                Iterator<String> keys = trackedFields.keySet().iterator();
                Map<String, Object> changedTrackedFields = new HashMap<>();
                while (keys.hasNext()) {                    
                    String fieldName = keys.next();
                    Object oldValue = trackedFields.get(fieldName);
                    Object newValue = Reflector.getFieldValue(entity, fieldName);
                    if (oldValue == null && newValue == null) {
                        continue;
                    }

                    if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
                        changedTrackedFields.put(fieldName, trackedFields.get(fieldName));
                        trackedFields.put(fieldName, newValue);
                    }
                }
                List<MappedEvent<Object, Object>> mappedEvents = (List<MappedEvent<Object, Object>>) method.invoke(entity,
                        changedTrackedFields);
                publish(mappedEvents);
                return;
            }
        }
    }

    @PostLoad
    @Logged
    public void onPostLoad(Object entity) throws Exception {
        setTrackedFields(entity);
    }

    @PostRemove
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostRemove(Object o) throws Exception {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnDelete.class)) {
                List<MappedEvent<Object, Object>> mappedEvents = (List<MappedEvent<Object, Object>>) method.invoke(o);
                publish(mappedEvents);
                return;
            }
        }
    }

    private void publish(List<MappedEvent<Object, Object>> mappedEvents) {
        mappedEvents.forEach(mappedEvent -> eventProducer.publish(mappedEvent.getKey(),
                mappedEvent.getPayload(), mappedEvent.getLabel(), mappedEvent.getStreams()));
    }

    private void setTrackedFields(Object entity) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();
        Trackable trackableEntity = (Trackable)entity;
        Map<String, Object> trackedFields = trackableEntity.getTrackedFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Track.class)) {
                Object fieldValue = Reflector.getFieldValue(entity, field.getName());
                trackedFields.put(field.getName(), fieldValue);
            }
        }
    }

}
