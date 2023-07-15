package io.microhooks.core.internal;

import io.microhooks.core.Event;
import io.microhooks.core.internal.util.Config;

public abstract class EventProducer {

    private String serviceName = Config.getServiceName();

    public void publish(long id, Object payload, String label, String[] streams) {

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
            publish(id, event, serviceName + "-" + streams[i]);
        }
    }

    public void publish(long id, Object payload, String[] streams) {
        publish(id, payload, null, streams);
    }

    public void publish(long id, Object payload, String label, String stream) {
        publish(id, payload, label, new String[] {stream});
    }

    public void publish(long id, Object payload, String stream) {
        publish(id, payload, null, stream);
    }

    protected abstract void publish(long id, Event<Object> event, String stream);

}
