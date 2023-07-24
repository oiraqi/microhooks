package io.microhooks.internal;


import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.common.Event;
import io.microhooks.internal.util.CachingReflector;
import io.microhooks.internal.util.logging.Logged;

public class SourceListener extends Listener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostPersist
    @Logged
    public void onPostPersist(Object entity) throws Exception {
        publish(entity, Event.RECORD_CREATED);
    }

    @PostUpdate
    @Logged
    public void onPostUpdate(Object entity) throws Exception {
        publish(entity, Event.RECORD_UPDATED);
    }

    @PostRemove
    @Logged
    public void onPostRemove(Object entity) throws Exception {
        publish(entity, Event.RECORD_DELETED);
    }

    private void publish(Object entity, String operation) throws Exception {
        long id = CachingReflector.getId(entity);
        Iterator<Entry<String, Entry<Class<?>, Boolean>>> iterator = CachingReflector.getSourceMappings(entity).entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<String, Entry<Class<?>, Boolean>> mapping = iterator.next();
            String stream = mapping.getKey();
            Class<?> projectionClass = mapping.getValue().getKey();
            boolean addOwnerToEvent = mapping.getValue().getValue();
            Object projection = objectMapper.convertValue(entity, projectionClass);
            Event<Object> event = new Event<>(projection, operation, addOwnerToEvent);
            getEventProducer().publish(id, event, stream);
        }
    }

}
