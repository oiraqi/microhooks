package io.microhooks.core.internal;

import io.microhooks.core.Event;
import io.microhooks.core.internal.util.Config;

public abstract class EventProducer {

    private String serviceName = Config.getServiceName();

    public void publish(long id, Event<Object> event, String stream) {

        if (stream == null || stream.isEmpty()) {
            throw new IllegalArgumentException("No stream may be null or empty");
        }
        publish(id, event, serviceName + "-" + stream);
    }

    protected abstract void doPublish(long id, Event<Object> event, String stream);

}
