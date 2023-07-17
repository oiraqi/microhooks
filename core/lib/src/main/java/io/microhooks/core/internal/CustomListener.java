package io.microhooks.core.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import io.microhooks.core.Event;
import io.microhooks.core.internal.util.CachingReflector;
import io.microhooks.core.internal.util.logging.Logged;
import io.microhooks.producer.ProduceEventOnCreate;
import io.microhooks.producer.ProduceEventOnDelete;
import io.microhooks.producer.ProduceEventOnUpdate;

public class CustomListener extends Listener {

    @PostPersist
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostPersist(Object entity) throws Exception {
        setTrackedFields(entity);
        for (Method method : CachingReflector.getOnCreateMethods(entity)) {
            Event<Object> event = (Event<Object>) method.invoke(entity);
            long id = CachingReflector.getId(entity);
            getEventProducer().publish(id, event, method.getAnnotation(ProduceEventOnCreate.class).stream());
            // Don't return here as we allow several methods to be annotated with OnCreate
        }
    }

    @PostUpdate
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostUpdate(Object entity) throws Exception {
        Map<String, Object> trackedFields = ((Trackable) entity).getMicrohooksTrackedFields();
        if (trackedFields == null) {
            return;
        }

        for (Method method : CachingReflector.getOnUpdateMethods(entity)) {            
            Iterator<String> keys = trackedFields.keySet().iterator();
            Map<String, Object> changedTrackedFields = new HashMap<>();
            while (keys.hasNext()) {
                String fieldName = keys.next();
                Object oldValue = trackedFields.get(fieldName);
                Object newValue = CachingReflector.getFieldValue(entity, fieldName);
                if (oldValue == null && newValue == null) {
                    continue;
                }

                if (oldValue == null || !oldValue.equals(newValue)) {
                    changedTrackedFields.put(fieldName, trackedFields.get(fieldName));
                    // Highly-concurrent thread safe
                    trackedFields.put(fieldName, newValue);
                }
            }
            Event<Object> event = (Event<Object>) method.invoke(entity,
                    changedTrackedFields);

            long id = CachingReflector.getId(entity);
            getEventProducer().publish(id, event, method.getAnnotation(ProduceEventOnUpdate.class).stream());
            // Don't return here as we allow several methods to be annotated with OnUpdate
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
        for (Method method : CachingReflector.getOnDeleteMethods(entity)) {
            Event<Object> event = (Event<Object>) method.invoke(entity);
            long id = CachingReflector.getId(entity);
            getEventProducer().publish(id, event, method.getAnnotation(ProduceEventOnDelete.class).stream());
            // Don't return here as we allow several methods to be annotated with OnDelete
        }
    }

    // This method is called only once per entity lifecycle, when loaded
    private void setTrackedFields(Object entity) throws Exception {

        Vector<String> trackedFieldsNames = CachingReflector.getTrackedFields(entity);
        if (trackedFieldsNames == null) {
            return;
        }

        Trackable trackableEntity = (Trackable) entity;
        // Highly-concurrent thread-safe
        Map<String, Object> trackedFields = new ConcurrentHashMap<>();
        for (int i = 0; i < trackedFieldsNames.size(); i++) {
            String filedName = trackedFieldsNames.get(i);
            Object fieldValue = CachingReflector.getFieldValue(entity, filedName);
            // Highly-concurrent thread safe
            trackedFields.put(filedName, fieldValue);
        }
        trackableEntity.setMicrohooksTrackedFields(trackedFields);
    }

}
