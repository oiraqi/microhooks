package io.microhooks.internal;


import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.common.Event;
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
        long id = Context.getId(entity);
        Iterator<Entry<String, Class<?>>> iterator = Context.getSourceMappings(entity).entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<String, Class<?>> mapping = iterator.next();
            String stream = mapping.getKey();
            Class<?> projectionClass = mapping.getValue();
            Object projection = objectMapper.convertValue(entity, projectionClass);
            Event<Object> event = new Event<>(projection, operation);
            getEventProducer().publish(id, event, stream);
        }
    }

}
