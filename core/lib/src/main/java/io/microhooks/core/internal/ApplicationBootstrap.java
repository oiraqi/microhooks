package io.microhooks.core.internal;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.atteo.classindex.ClassIndex;

import io.microhooks.consumer.Sink;
import io.microhooks.core.internal.util.Config;

public class ApplicationBootstrap {

    @PersistenceContext
    EntityManager em;

    private Map<String, ArrayList<Class<?>>> sinkMap;
    private Map<String, ArrayList<Class<?>>> customSinkMap;

    //Callback exposed to the underlying container (Spring, Quarkus, Micronaut, ...)
    public void setup() throws Exception {
        buildSinkMap();
        buildCustomSinkMap();
        
        Config.getEventConsumer().subscribe(em, sinkMap, customSinkMap);
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    protected Map<String, ArrayList<Class<?>>> getSinkMap() {
        if (sinkMap == null) {
            buildSinkMap();
        }
        return sinkMap;
    }

    protected Map<String, ArrayList<Class<?>>> getCustomSinkMap() {
        if (customSinkMap == null) {
            buildCustomSinkMap();
        }
        return customSinkMap;
    }

    private void buildSinkMap() {
        if (sinkMap != null) {
            return;
        }

        sinkMap = new HashMap<>();
        Iterable<Class<?>> sinks = ClassIndex.getAnnotated(Sink.class);
        for (Class<?> sink : sinks) {
            Sink sinkAnnotation = sink.<Sink>getAnnotation(Sink.class);
            String stream = sinkAnnotation.stream();
            if (sinkMap.containsKey(stream)) {
                sinkMap.get(stream).add(sink);
            } else {
                ArrayList<Class<?>> list = new ArrayList<>();
                list.add(sink);
                sinkMap.put(stream, list);
            }
        }
    }

    private void buildCustomSinkMap() {
        if (customSinkMap != null) {
            return;
        }

        /*customSinkMap = new HashMap<>();
        Iterable<Class<?>> customSinks = ClassIndex.getAnnotated(CustomSink.class);
        for (Class<?> customSink : customSinks) {
            CustomSink customSinkAnnotation = customSink.<CustomSink>getAnnotation(CustomSink.class);
            String stream = customSinkAnnotation.stream();
            if (customSinkMap.containsKey(stream)) {
                customSinkMap.get(stream).add(customSink);
            } else {
                ArrayList list = new ArrayList();
                list.add(customSink);
                customSinkMap.put(stream, list);
            }
        }*/
    }
    
}
