package io.microhooks.internal;

import io.microhooks.common.Event;
import io.microhooks.internal.util.Config;

public abstract class EventProducer {

    private String serviceName = Config.getServiceName();

    public void publish(long id, Event<Object> event, String stream) {

        if (stream == null || stream.isEmpty()) {
            throw new IllegalArgumentException("No stream may be null or empty");
        }
        doPublish(id, event, serviceName + "-" + stream);
    }

    public void publish(long id, Event<Object> event, String[] streams) {
        if (streams == null) {
            return;
        }
        for (String stream : streams) {
            publish(id, event, stream);
        }
    }

    public void publishTx(long id, Event<Object> event, String[] streams) {
        publish(id, new TransactionalEvent<>(event), streams);
    }

    protected abstract void doPublish(long id, Event<Object> event, String stream);

}
