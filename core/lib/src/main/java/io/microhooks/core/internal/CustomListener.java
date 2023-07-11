package io.microhooks.core.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import io.microhooks.core.Event;
import io.microhooks.core.internal.util.Reflector;
import io.microhooks.core.internal.util.logging.Logged;
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
                Event<Object> event = (Event<Object>) method.invoke(entity);
                String key = getId(entity).toString();
                getEventProducer().publish(key, event, method.getAnnotation(OnCreate.class).stream());
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
                        //Highly-concurrent thread safe
                        trackedFields.put(fieldName, newValue);
                    }
                }
                Event<Object> event = (Event<Object>) method.invoke(entity,
                        changedTrackedFields);
                String key = getId(entity).toString();
                getEventProducer().publish(key, event, method.getAnnotation(OnUpdate.class).stream());
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
                Event<Object> event = (Event<Object>) method.invoke(entity);
                String key = getId(entity).toString();
                getEventProducer().publish(key, event, method.getAnnotation(OnDelete.class).stream());
                // Don't return here as we allow several methods to be annotated with OnDelete
            }
        }
    }

    private void setTrackedFields(Object entity) throws Exception {
        Class<?> cl = (Class<?>)entity.getClass();
        Field f = cl.getField("MICROHOOKS_TRACKED_FIELDS_NAMES");
        Vector<String> MICROHOOKS_TRACKED_FIELDS_NAMES = (Vector<String>)f.get(null);
        Field[] fields = entity.getClass().getDeclaredFields();
        if (MICROHOOKS_TRACKED_FIELDS_NAMES.isEmpty()) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(Track.class)) {
                    MICROHOOKS_TRACKED_FIELDS_NAMES.add(field.getName());
                }
            }
        }        
        Trackable trackableEntity = (Trackable)entity;
        // Highly-concurrent thread-safe
        Map<String, Object> trackedFields = new ConcurrentHashMap<>();
        
        for (int i = 0; i < MICROHOOKS_TRACKED_FIELDS_NAMES.size(); i++) {            
            String filedName = MICROHOOKS_TRACKED_FIELDS_NAMES.get(i);
            Object fieldValue = Reflector.getFieldValue(entity, filedName);
            //Highly-concurrent thread safe
            trackedFields.put(filedName, fieldValue);
        }
        trackableEntity.setMicrohooksTrackedFields(trackedFields);
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
