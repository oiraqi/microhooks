package io.microhooks.core.internal;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.atteo.classindex.ClassIndex;

import io.microhooks.consumer.Sink;

public abstract class EventSubscriptionSetup {

    private Map<String, ArrayList<Class<?>>> sinkMap;
    private Map<String, ArrayList<Class<?>>> customSinkMap;

    //Callback exposed to the underlying container
    public abstract void subscribe() throws Exception;

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
