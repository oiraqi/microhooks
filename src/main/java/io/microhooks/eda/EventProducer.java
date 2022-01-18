package io.microhooks.eda;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public abstract class EventProducer<T, U> {

    @Value("${appName}")
    private String appName;

    public void publish(T key, U payload, String label, String[] streams) {
        if (key == null) {
            throw new IllegalArgumentException("Key can't be null");
        }

        if (streams == null || streams.length == 0) {
            throw new IllegalArgumentException("Streams can't be null or empty");
        }

        for (String stream: streams) {
            if (stream == null || stream.isEmpty()) {
                throw new IllegalArgumentException("No stream may be null or empty");
            }
        }
        
        Event<U> event = new Event<>(payload, label);
        for (int i = 0; i < streams.length; i++) {
            publish(key, event, appName + "#" + streams[i]);
        }
    }

    public void publish(T key, U payload, String[] streams) {
        publish(key, payload, null, streams);
    }

    public void publish(T key, U payload, String label, String stream) {
        publish(key, payload, label, new String[] {stream});
    }

    public void publish(T key, U payload, String stream) {
        publish(key, payload, null, stream);
    }

    protected abstract void publish(T key, Event<U> event, String stream);

}
