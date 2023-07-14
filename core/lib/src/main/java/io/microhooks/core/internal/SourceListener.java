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
        Iterator<Entry<String, Class<?>>> iterator = CachingReflector.getSourceMappings(entity).entrySet().iterator();        
        while(iterator.hasNext()) {
            Entry<String, Class<?>> mapping = iterator.next();
            String stream = mapping.getKey();
            Class<?> dtoClass = mapping.getValue();
            Object dto = objectMapper.convertValue(entity, dtoClass);
            getEventProducer().publish(id, dto, operation, stream);
        }
    }

}
