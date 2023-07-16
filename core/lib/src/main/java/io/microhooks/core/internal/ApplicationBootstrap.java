package io.microhooks.core.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import io.microhooks.core.internal.util.CachingReflector;
import io.microhooks.core.internal.util.Config;

public class ApplicationBootstrap {

    @PersistenceContext
    protected EntityManager em;

    //Callback to be exposed to the underlying container (Spring, Quarkus, Micronaut, ...)
    //by the overriding container extension
    public void setup() throws Exception {      
        Config.getEventConsumer().launch(em);
        System.out.println(CachingReflector.getSinkMap());
    }

    protected EntityManager getEntityManager() {
        return em;
    }
    
}
