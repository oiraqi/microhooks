package io.microhooks.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import io.microhooks.common.Event;
import io.microhooks.internal.util.CachingReflector;
import io.microhooks.internal.util.logging.Logged;
import io.microhooks.source.ProduceEventOnCreate;
import io.microhooks.source.ProduceEventOnDelete;
import io.microhooks.source.ProduceEventOnUpdate;

public class CustomListener extends Listener {

    @PostPersist
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostPersist(Object entity) throws Exception {
        setTrackedFields(entity);
        for (Method method : CachingReflector.getProduceEventOnCreateMethods(entity)) {
            Event<Object> event = (Event<Object>) method.invoke(entity);
            long id = CachingReflector.getId(entity);
            getEventProducer().publish(id, event, method.getAnnotation(ProduceEventOnCreate.class).streams());
            // Don't return here as we allow several methods to be annotated with OnCreate
        }
        for (Method method : CachingReflector.getProduceEventsOnCreateMethods(entity)) {
            Map<Event<Object>, String[]> eventsToStream = (Map<Event<Object>, String[]>) method.invoke(entity);
            if (eventsToStream != null) {
                long id = CachingReflector.getId(entity);
                for (Entry<Event<Object>, String[]> entry : eventsToStream.entrySet()) {
                    getEventProducer().publish(id, entry.getKey(), entry.getValue());
                }
            }
            // Don't return here as we allow several methods to be annotated with OnUpdate
        }
    }

    @PostUpdate
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostUpdate(Object entity) throws Exception {
        Map<String, Object> trackedFields = ((Trackable) entity).getMicrohooksTrackedFields();
        if (trackedFields == null) { // entity is Trackable but didn't define any @Track fields
            return;
        }
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
        for (Method method : CachingReflector.getProduceEventOnUpdateMethods(entity)) {
            String[] streams = method.getAnnotation(ProduceEventOnUpdate.class).streams();
            Event<Object> event = (Event<Object>) method.invoke(entity, changedTrackedFields);
            if (event != null) {
                long id = CachingReflector.getId(entity);
                getEventProducer().publish(id, event, streams);
            }
            // Don't return here as we allow several methods to be annotated with OnUpdate
        }
        for (Method method : CachingReflector.getProduceEventsOnUpdateMethods(entity)) {
            Map<Event<Object>, String[]> eventsToStream = (Map<Event<Object>, String[]>) method.invoke(entity, changedTrackedFields);
            if (eventsToStream != null) {
                long id = CachingReflector.getId(entity);
                for (Entry<Event<Object>, String[]> entry : eventsToStream.entrySet()) {
                    getEventProducer().publish(id, entry.getKey(), entry.getValue());
                }
            }
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
        for (Method method : CachingReflector.getProduceEventOnDeleteMethods(entity)) {
            Event<Object> event = (Event<Object>) method.invoke(entity);
            long id = CachingReflector.getId(entity);
            getEventProducer().publish(id, event, method.getAnnotation(ProduceEventOnDelete.class).streams());
            // Don't return here as we allow several methods to be annotated with OnDelete
        }
        for (Method method : CachingReflector.getProduceEventsOnDeleteMethods(entity)) {
            Map<Event<Object>, String[]> eventsToStream = (Map<Event<Object>, String[]>) method.invoke(entity);
            if (eventsToStream != null) {
                long id = CachingReflector.getId(entity);
                for (Entry<Event<Object>, String[]> entry : eventsToStream.entrySet()) {
                    getEventProducer().publish(id, entry.getKey(), entry.getValue());
                }
            }
            // Don't return here as we allow several methods to be annotated with OnUpdate
        }
    }

    // This method is called only once per entity lifecycle, when loaded
    private void setTrackedFields(Object entity) throws Exception {

        Vector<String> trackedFieldsNames = CachingReflector.getTrackedFieldsNames(entity);
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
