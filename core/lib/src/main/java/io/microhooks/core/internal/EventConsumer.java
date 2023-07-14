package io.microhooks.core.internal;

import io.microhooks.core.Event;

import java.util.Map;
import java.util.ArrayList;

import javax.persistence.EntityManager;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class EventConsumer {

    private EntityManager em;
    private Map<String, ArrayList<Class<?>>> sinks;
    private Map<String, ArrayList<Class<?>>> customSinks;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void subscribe(EntityManager em, Map<String, ArrayList<Class<?>>> sinks,
            Map<String, ArrayList<Class<?>>> customSinks) {

        this.em = em;
        this.sinks = sinks;
        this.customSinks = customSinks;
        subscribeWithBroker();
    }

    protected void processEvent(long sourceId, Event<Object> event, String stream) {

        ArrayList<Class<?>> sinkEntityClasses = sinks.get(stream);
        if (sinkEntityClasses != null && event.getLabel() != null) {
            if (event.getLabel().equals(Event.RECORD_CREATED)) {
                for (int i = 0; i < sinkEntityClasses.size(); i++) {
                    Class<?> sinkEntityClass = sinkEntityClasses.get(i);
                    try {
                        Object sinkEntity = objectMapper.convertValue(event.getPayload(), sinkEntityClass);
                        ((Sinkable) sinkEntity).setMicrohooksSourceId(sourceId);
                        em.persist(sinkEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }                    
                }
            }
        }

    }

    protected abstract void subscribeWithBroker();

}
