package io.microhooks.internal;


import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.common.Event;
import io.microhooks.internal.util.logging.Logged;

public class SourceListener extends EntityListener {

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
        Context.getSourceMappings(entity).entrySet().forEach(mapping -> {
            String stream = mapping.getKey();
            Class<?> projectionClass = mapping.getValue();
            Object projection = objectMapper.convertValue(entity, projectionClass);
            Event<Object> event = new Event<>(projection, operation);
            getEventProducer().publish(id, event, stream);
        });
    }

}
