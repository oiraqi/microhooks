package io.microhooks.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import io.microhooks.core.Event;
import io.microhooks.internal.util.Reflector;
import io.microhooks.internal.util.logging.Logged;
import io.microhooks.producer.OnCreate;
import io.microhooks.producer.OnDelete;
import io.microhooks.producer.OnUpdate;
import io.microhooks.producer.Track;

public class CustomListener extends Listener {

    @PostPersist
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostPersist(Object entity) throws Exception {
        setTrackedFields(entity);
        for (Method method : entity.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnCreate.class)) {                
                List<Event<Object>> events = (List<Event<Object>>) method.invoke(entity);
                String key = getId(entity).toString();
                publish(key, events, method.getAnnotation(OnCreate.class).streams());
                // Don't return here as we allow several methods to be annotated with OnCreate
            }
        }
    }

    @PostUpdate
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostUpdate(Object entity) throws Exception {
        for (Method method : entity.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnUpdate.class)) {
                Map<String, Object> trackedFields = ((Trackable)entity).getMicrohooksTrackedFields();
                Iterator<String> keys = trackedFields.keySet().iterator();
                Map<String, Object> changedTrackedFields = new HashMap<>();
                while (keys.hasNext()) {                    
                    String fieldName = keys.next();
                    Object oldValue = trackedFields.get(fieldName);
                    Object newValue = Reflector.getFieldValue(entity, fieldName);
                    if (oldValue == null && newValue == null) {
                        continue;
                    }

                    if (oldValue == null || !oldValue.equals(newValue)) {
                        changedTrackedFields.put(fieldName, trackedFields.get(fieldName));
                        trackedFields.put(fieldName, newValue);
                    }
                }
                List<Event<Object>> events = (List<Event<Object>>) method.invoke(entity,
                        changedTrackedFields);
                String key = getId(entity).toString();
                publish(key, events, method.getAnnotation(OnUpdate.class).streams());
                // Don't return here as we allow several methods to be annotated with OnUpdate
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
    public void onPostRemove(Object entity) throws Exception {
        for (Method method : entity.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnDelete.class)) {                
                List<Event<Object>> events = (List<Event<Object>>) method.invoke(entity);
                String key = getId(entity).toString();
                publish(key, events, method.getAnnotation(OnDelete.class).streams());
                // Don't return here as we allow several methods to be annotated with OnDelete
            }
        }
    }

    private void publish(String key, List<Event<Object>> events, String[] streams) {
        events.forEach(event -> getEventProducer().publish(key,
                event.getPayload(), event.getLabel(), streams));
    }

    private void setTrackedFields(Object entity) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();
        Trackable trackableEntity = (Trackable)entity;
        Map<String, Object> trackedFields = trackableEntity.getMicrohooksTrackedFields();
        if (trackedFields == null) {
            trackableEntity.setMicrohooksTrackedFields(new HashMap<>());
            trackedFields = trackableEntity.getMicrohooksTrackedFields();
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(Track.class)) {
                Object fieldValue = Reflector.getFieldValue(entity, field.getName());
                trackedFields.put(field.getName(), fieldValue);
            }
        }
    }

    private Object getId(Object entity) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                return Reflector.getFieldValue(entity, field.getName());
            }
        }
        throw new IdNotFoundException();
    }

}
