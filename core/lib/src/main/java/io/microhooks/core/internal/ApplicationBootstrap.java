package io.microhooks.core.internal;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.atteo.classindex.ClassIndex;

import io.microhooks.consumer.Sink;
import io.microhooks.core.ConfigOption;
import io.microhooks.core.internal.util.Config;

public class ApplicationBootstrap {

    @PersistenceContext
    EntityManager em;

    private Map<String, Map<Class<?>, String>> sinkMap; //<stream -- <entityClass -- authenticationKey>>
    private Map<String, ArrayList<Class<?>>> customSinkMap;

    //Callback to be exposed to the underlying container (Spring, Quarkus, Micronaut, ...)
    //by the overriding container extension
    public void setup() throws Exception {
        buildSinkMap();
        buildCustomSinkMap();        
        Config.getEventConsumer().subscribe(em, sinkMap, customSinkMap);
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    protected Map<String, Map<Class<?>, String>> getSinkMap() {
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
            ConfigOption authenticateOption = sinkAnnotation.authenticate();
            String key = "";
            if(authenticateOption == ConfigOption.ENABLED ||
                (authenticateOption == ConfigOption.APP && Config.getAuthenticate())) {
                key = sinkAnnotation.authenticationKey();
                if (key.equals("")) {
                    key = Config.getAuthenticationKey();
                }
            }
            if (sinkMap.containsKey(stream)) {
                sinkMap.get(stream).put(sink, key);
            } else {
                Map<Class<?>, String> map = new HashMap<>();
                map.put(sink, key);
                sinkMap.put(stream, map);
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
