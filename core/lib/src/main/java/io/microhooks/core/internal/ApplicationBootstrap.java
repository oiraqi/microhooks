package io.microhooks.core.internal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.microhooks.core.internal.util.CachingReflector;
import io.microhooks.core.internal.util.Config;

public class ApplicationBootstrap {

    @PersistenceContext
    EntityManager em;

    //Callback to be exposed to the underlying container (Spring, Quarkus, Micronaut, ...)
    //by the overriding container extension
    public void setup() throws Exception {      
        Config.getEventConsumer().subscribe(em, CachingReflector.getSinkMap(), null);
    }

    protected EntityManager getEntityManager() {
        return em;
    }
    
}
