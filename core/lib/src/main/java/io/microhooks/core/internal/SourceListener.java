package io.microhooks.core.internal;


import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import io.microhooks.core.Event;
import io.microhooks.core.internal.util.CachingReflector;
import io.microhooks.core.internal.util.logging.Logged;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SourceListener extends Listener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostPersist
    @Logged
    public void onPostPersist(Object entity) throws Exception {
        publish(entity, Event.RECORD_CREATED);
        System.out.println(entity);
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
        boolean sign = CachingReflector.getSign(entity);
        while(iterator.hasNext()) {
            Entry<String, Entry<Class<?>, Boolean>> mapping = iterator.next();
            String stream = mapping.getKey();
            Class<?> dtoClass = mapping.getValue().getKey();
            boolean addOwnerToEvent = mapping.getValue().getValue();
            Object dto = objectMapper.convertValue(entity, dtoClass);
            Event<Object> event = new Event<>(dto, operation, addOwnerToEvent);
            if (sign) {
                event.sign(id);
            }
            getEventProducer().publish(id, event, stream);
        }
    }

}
