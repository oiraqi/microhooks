package io.microhooks.internal;


import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.common.Event;
//import io.microhooks.internal.util.logging.Logged;

import io.microhooks.internal.util.Monitor;

public class SourceListener extends EntityListener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostPersist
    //@Logged
    public void onPostPersist(Object entity) throws Exception {
        long startTime = System.nanoTime();
        publish(entity, Event.RECORD_CREATED);
        long endTime = System.nanoTime();
        Monitor.sourceTotalTime += endTime - startTime;
        Monitor.sourceCount++;

        if (Monitor.sourceCount % 1000 == 0) {
            System.out.println("avg: " + (float)Monitor.sourceTotalTime / Monitor.sourceCount);
        }
    }

    @PostUpdate
    //@Logged
    public void onPostUpdate(Object entity) throws Exception {
        publish(entity, Event.RECORD_UPDATED);
    }

    @PostRemove
    //@Logged
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
