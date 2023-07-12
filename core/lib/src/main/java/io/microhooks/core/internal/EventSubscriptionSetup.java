package io.microhooks.core.internal;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.atteo.classindex.ClassIndex;

import io.microhooks.core.internal.util.Config;
import io.microhooks.consumer.Sink;
import io.microhooks.consumer.CustomSink;

public abstract class EventSubscriptionSetup {

    private Map<String, ArrayList<Class<?>>> sinkMap;
    private Map<String, ArrayList<Class<?>>> customSinkMap;
    private EventConsumer eventConsumer;

    public abstract void subscribe();

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

    protected EventConsumer getEventConsumer() {
        if (eventConsumer == null) {
            try {
                eventConsumer = Config.getEventConsumer();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return eventConsumer;
    }

    private void buildSinkMap() {
        sinkMap = new HashMap<>();
        Iterable<Class<?>> sinks = ClassIndex.getAnnotated(Sink.class);
        for (Class<?> sink : sinks) {
        }
    }

    private void buildCustomSinkMap() {
        
    }
    
}
