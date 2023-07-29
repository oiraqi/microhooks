package io.microhooks.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import io.microhooks.common.Event;
import io.microhooks.internal.util.logging.Logged;
import io.microhooks.source.ProduceEventOnCreate;
import io.microhooks.source.ProduceEventOnDelete;
import io.microhooks.source.ProduceEventOnUpdate;

public class CustomSourceListener extends EntityListener {

    @PostPersist
    @Logged
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
        Map<String, String> trackedFields = ((Trackable) entity).getMicrohooksTrackedFields();
        if (trackedFields == null) { // entity is Trackable but didn't define any @Track fields
            return;
        }
        Iterator<String> keys = trackedFields.keySet().iterator();
        Map<String, Object> changedTrackedFields = new HashMap<>();
        while (keys.hasNext()) {
            String fieldName = keys.next();
            String oldValue = trackedFields.get(fieldName);
            String newValue = BeanUtils.getProperty(entity, fieldName);
            if (oldValue == null && newValue == null) {
                continue;
            }
            if (oldValue == null || !oldValue.equals(newValue)) {
                changedTrackedFields.put(fieldName, trackedFields.get(fieldName));
                // Highly-concurrent thread safe
                trackedFields.put(fieldName, newValue);
            }
        }
        for (Method method : Context.getProduceEventOnUpdateMethods(entity)) {
            String[] streams = method.getAnnotation(ProduceEventOnUpdate.class).streams();
            Event<Object> event = (Event<Object>) method.invoke(entity, changedTrackedFields);
            if (event != null) {
                long id = Context.getId(entity);
                getEventProducer().publish(id, event, streams);
            }
            // Don't return here as we allow several methods to be annotated with OnUpdate
        }
        for (Method method : Context.getProduceEventsOnUpdateMethods(entity)) {
            Map<Event<Object>, String[]> eventsToStream = (Map<Event<Object>, String[]>) method.invoke(entity, changedTrackedFields);
            if (eventsToStream != null) {
                long id = Context.getId(entity);
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
                for (Entry<Event<Object>, String[]> entry : eventsToStream.entrySet()) {
                    getEventProducer().publish(id, entry.getKey(), entry.getValue());
                }
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
