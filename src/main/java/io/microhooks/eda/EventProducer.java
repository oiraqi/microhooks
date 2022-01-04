package io.microhooks.eda;

import org.springframework.stereotype.Component;

@Component
public abstract class EventProducer<T, U> {

    public void publish(T key, U payload, String label, String[] streams) {
        if (key == null) {
            throw new IllegalArgumentException("Key can't be null");
        }

        if (streams == null || streams.length == 0) {
            throw new IllegalArgumentException("Streams can't be null or empty");
        }
        
        Event<T, U> event = new Event<>(key, payload, label);
        for (int i = 0; i < streams.length; i++) {
            publish(event, "${appName}#" + streams[i]);
        }
    }

    public void publish(T key, U payload, String[] streams) {
        publish(key, payload, null, streams);
    }

    public void publish(U payload, String[] streams) {
        publish(null, payload, null, streams);
    }

    public abstract void publish(Event<T, U> event, String stream);

}
