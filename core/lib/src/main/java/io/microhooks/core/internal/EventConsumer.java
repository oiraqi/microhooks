package io.microhooks.core.internal;

import java.util.Map;
import java.util.ArrayList;

import javax.persistence.EntityManager;

public interface EventConsumer {

    void subscribe(EntityManager em, Map<String, ArrayList<Class<?>>> sinks, Map<String, ArrayList<Class<?>>> customSinks);
    
}
