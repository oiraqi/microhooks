package io.microhooks.source;

import io.microhooks.common.Event;
import io.microhooks.internal.util.Config;

public class EventProducer {

    private io.microhooks.internal.EventProducer producer;

    public EventProducer() throws Exception {
        producer = Config.getEventProducer();
    }

    public void publish(long id, Event<Object> event, String stream) {
        producer.publish(id, event, stream);
    }

    public void publish(long id, Event<Object> event, String[] streams) {
        producer.publish(id, event, streams);
    }

    public void publishTx(long id, Event<Object> event, String[] streams) {
        producer.publishTx(id, event, streams);
    }
    
}
