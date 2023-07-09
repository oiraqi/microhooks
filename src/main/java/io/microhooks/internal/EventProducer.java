package io.microhooks.internal;

import io.microhooks.core.Event;
import io.microhooks.internal.util.Config;

public abstract class EventProducer {

    private final String appName = Config.getAppName();

    public void publish(String key, Object payload, String label, String[] streams) {
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
        
        Event<Object> event = new Event<Object>(payload, label);
        for (int i = 0; i < streams.length; i++) {
            publish(key, event, appName + "#" + streams[i]);
        }
    }

    public void publish(String key, Object payload, String[] streams) {
        publish(key, payload, null, streams);
    }

    public void publish(String key, Object payload, String label, String stream) {
        publish(key, payload, label, new String[] {stream});
    }

    public void publish(String key, Object payload, String stream) {
        publish(key, payload, null, stream);
    }

    protected abstract void publish(String key, Event<Object> event, String stream);

}
