package io.microhooks.core.internal;

import io.microhooks.core.Event;

import java.util.Map;
import java.util.ArrayList;

import javax.persistence.EntityManager;

import javax.persistence.EntityManager;

public abstract class EventConsumer {

    private EntityManager em;
    private Map<String, ArrayList<Class<?>>> sinks;
    private Map<String, ArrayList<Class<?>>> customSinks;

    public void subscribe(EntityManager em, Map<String, ArrayList<Class<?>>> sinks,
                        Map<String, ArrayList<Class<?>>> customSinks) {

        this.em = em;
        this.sinks = sinks;
        this.customSinks = customSinks;
        subscribeWithBroker();
    }

    public void processEvent(Event event, String stream) {

    }

    protected abstract void subscribeWithBroker();
    
}
