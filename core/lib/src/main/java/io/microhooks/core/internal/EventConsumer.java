package io.microhooks.core.internal;

import io.microhooks.core.Event;
import io.microhooks.core.internal.util.CachingReflector;

import java.util.Map;

import jakarta.persistence.EntityManager;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class EventConsumer {

    private EntityManager em;
    private EventRepository eventRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void launch(EventRepository eventRepository, EntityManager em) {

        this.em = em;
        this.eventRepository = eventRepository;
        subscribe();
    }

    public void processEvent(long sourceId, Event<JsonNode> event, String stream) {
        Map<Class<?>, String> sinkEntityClassMap = CachingReflector.getSinkMap().get(stream);
        if (sinkEntityClassMap != null && event.getLabel() != null) {
            if (event.getLabel().equals(Event.RECORD_CREATED)) {
                Iterator<Class<?>> sinkEntityClassIterator = sinkEntityClassMap.keySet().iterator();
                while (sinkEntityClassIterator.hasNext()) {
                    Class<?> sinkEntityClass = sinkEntityClassIterator.next();
                    String authenticationKey = sinkEntityClassMap.get(sinkEntityClass);
                    if (!authenticationKey.equals("") && !event.verify(sourceId, authenticationKey))
                            continue;
                    
                    try {
                        Object sinkEntity = objectMapper.convertValue(event.getPayload(), sinkEntityClass);
                        ((Sinkable) sinkEntity).setMicrohooksSourceId(sourceId);
                        eventRepository.save(sinkEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }                    
                }
            }
        }

    }

    protected abstract void subscribe();

}
