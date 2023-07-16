package io.microhooks.core.internal;

import io.microhooks.core.Event;
import io.microhooks.core.internal.util.CachingReflector;
import io.microhooks.core.internal.util.Security;

import java.util.Map;

import jakarta.persistence.EntityManager;
import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class EventConsumer {

    private EntityManager em;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void launch(EntityManager em) {

        this.em = em;
        subscribe();
    }

    protected void processEvent(long sourceId, Event<Object> event, String stream) {

        Map<Class<?>, String> sinkEntityClassMap = CachingReflector.getSinkMap().get(stream);
        if (sinkEntityClassMap != null && event.getLabel() != null) {
            if (event.getLabel().equals(Event.RECORD_CREATED)) {
                Iterator<Class<?>> sinkEntityClassIterator = sinkEntityClassMap.keySet().iterator();
                while (sinkEntityClassIterator.hasNext()) {
                    Class<?> sinkEntityClass = sinkEntityClassIterator.next();
                    String authenticationKey = sinkEntityClassMap.get(sinkEntityClass);
                    if (!authenticationKey.equals("") && !Security.verify(sourceId, event, authenticationKey))
                            continue;
                    
                    try {
                        Object sinkEntity = objectMapper.convertValue(event.getPayload(), sinkEntityClass);
                        ((Sinkable) sinkEntity).setMicrohooksSourceId(sourceId);
                        System.out.println(sinkEntity);
                        // em.persist(sinkEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }                    
                }
            }
        }

    }

    protected abstract void subscribe();

}
