package io.microhooks.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import io.microhooks.common.Event;
import io.microhooks.internal.util.Monitor;
//import io.microhooks.internal.util.logging.Logged;
import io.microhooks.source.ProduceEventOnCreate;
import io.microhooks.source.ProduceEventOnDelete;
import io.microhooks.source.ProduceEventOnUpdate;

public class CustomSourceListener extends EntityListener {

    @PostPersist
    //@Logged
    @SuppressWarnings("unchecked")
    public void onPostPersist(Object entity) throws Exception {
        setTrackedFields(entity);
        for (Method method : Context.getProduceEventOnCreateMethods(entity)) {
            Event<Object> event = (Event<Object>) method.invoke(entity);
            long id = Context.getId(entity);
            getEventProducer().publish(id, event, method.getAnnotation(ProduceEventOnCreate.class).streams());
            // Don't return here as we allow several methods to be annotated with OnCreate
        }
        for (Method method : Context.getProduceEventsOnCreateMethods(entity)) {
            Map<Event<Object>, String[]> eventsToStream = (Map<Event<Object>, String[]>) method.invoke(entity);
            if (eventsToStream != null) {
                long id = Context.getId(entity);
                eventsToStream.entrySet().forEach(entry -> {
                    getEventProducer().publish(id, entry.getKey(), entry.getValue());
                });
            }
            // Don't return here as we allow several methods to be annotated with OnUpdate
        }
    }

    @PostUpdate
    //@Logged
    @SuppressWarnings("unchecked")
    public void onPostUpdate(Object entity) throws Exception {
        long startTime = System.nanoTime();
        Map<String, String> trackedFields = ((Trackable) entity).getMicrohooksTrackedFields();
        if (trackedFields == null) { // entity is Trackable but didn't define any @Track fields
            return;
        }
        final Map<String, String> changedTrackedFields = new HashMap<>();
        trackedFields.entrySet().forEach(entry -> {
            try {
                String fieldName = entry.getKey();
                String oldValue = entry.getValue();
                String newValue = BeanUtils.getProperty(entity, fieldName);
                if (oldValue == null && newValue == null) {
                    return;
                }
                if (oldValue == null || !oldValue.equals(newValue)) {
                    changedTrackedFields.put(fieldName, trackedFields.get(fieldName));
                    // Highly-concurrent thread safe
                    trackedFields.put(fieldName, newValue);
                }
            } catch(Exception ex) {
            }
        });
        for (Method method : Context.getProduceEventOnUpdateMethods(entity)) {      
            Event<Object> event = (Event<Object>) method.invoke(entity, changedTrackedFields);
            if (event != null) {
                String[] streams = method.getAnnotation(ProduceEventOnUpdate.class).streams();
                long id = Context.getId(entity);
                getEventProducer().publish(id, event, streams);
            }
            // Don't return here as we allow several methods to be annotated with OnUpdate
        }
        for (Method method : Context.getProduceEventsOnUpdateMethods(entity)) {
            Map<Event<Object>, String[]> eventsToStream = (Map<Event<Object>, String[]>) method.invoke(entity, changedTrackedFields);
            if (eventsToStream != null) {
                long id = Context.getId(entity);
                eventsToStream.entrySet().forEach(entry -> {
                    getEventProducer().publish(id, entry.getKey(), entry.getValue());
                });
            }
            // Don't return here as we allow several methods to be annotated with OnUpdate
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        Monitor.customTotalTime += duration;

        Monitor.customCount++;

        if (Monitor.customCount % 1000 == 0) {
            System.out.println("duration: " + duration);
            System.out.println("custom count: " + Monitor.customCount);
            System.out.println("custom total: " + Monitor.customTotalTime);
            System.out.println("custom avg: " + (float)Monitor.customTotalTime / Monitor.customCount);
        }
    }

    @PostLoad
    //@Logged
    public void onPostLoad(Object entity) throws Exception {
        setTrackedFields(entity);
    }

    @PostRemove
    //@Logged
    @SuppressWarnings("unchecked")
    public void onPostRemove(Object entity) throws Exception {
        for (Method method : Context.getProduceEventOnDeleteMethods(entity)) {
            Event<Object> event = (Event<Object>) method.invoke(entity);
            long id = Context.getId(entity);
            getEventProducer().publish(id, event, method.getAnnotation(ProduceEventOnDelete.class).streams());
            // Don't return here as we allow several methods to be annotated with OnDelete
        }
        for (Method method : Context.getProduceEventsOnDeleteMethods(entity)) {
            Map<Event<Object>, String[]> eventsToStream = (Map<Event<Object>, String[]>) method.invoke(entity);
            if (eventsToStream != null) {
                long id = Context.getId(entity);
                eventsToStream.entrySet().forEach(entry -> {
                    getEventProducer().publish(id, entry.getKey(), entry.getValue());
                });
            }
            // Don't return here as we allow several methods to be annotated with OnUpdate
        }
    }

    // This method is called only once per entity lifecycle, when loaded
    private void setTrackedFields(Object entity) throws Exception {

        Set<String> trackedFieldsNames = Context.getTrackedFieldsNames(entity);
        if (trackedFieldsNames.isEmpty()) {
            return;
        }

        Trackable trackableEntity = (Trackable) entity;
        // Highly-concurrent thread-safe
        Map<String, String> trackedFields = new ConcurrentHashMap<>();
        for (String fieldName : trackedFieldsNames) {
            // fieldValue is not necessarily a String, but it's OK for
            // comparison as both olValue and newValue are converted to Strings
            String fieldValue = BeanUtils.getProperty(entity, fieldName);
            // Highly-concurrent thread safe
            trackedFields.put(fieldName, fieldValue);
        }
        trackableEntity.setMicrohooksTrackedFields(trackedFields);
    }

}
