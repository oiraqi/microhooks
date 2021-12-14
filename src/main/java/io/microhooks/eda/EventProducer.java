package io.microhooks.eda;

import org.springframework.beans.factory.annotation.Value;

public abstract class EventProducer<T, U> {

    @Value("${io.microhooks.providers.broker.cluster}")
    protected String brokers;

    public void publish(T key, U payload, String label, String[] streams) {   
        if (streams == null || streams.length == 0) {
            throw new IllegalArgumentException("Streams can't be null or empty");
        }
        
        Event<T, U> event = new Event<>(key, payload, label);
        for (int i = 0; i < streams.length; i++) {
            publish(key, event, "${appName}#" + streams[i]);
        }
    }

    public void publish(U payload, String label, String[] streams) {
        publish(null, payload, label, streams);
    }

    public void publish(T key, U payload, String[] streams) {
        publish(key, payload, null, streams);
    }

    public void publish(U payload, String[] streams) {
        publish(null, payload, null, streams);
    }

    protected abstract void publish(T key, Event<T, U> event, String stream);

}
