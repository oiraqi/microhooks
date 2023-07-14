package io.microhooks.brokers.kafka;

import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;

import io.microhooks.core.internal.util.JsonSerdes;
import io.microhooks.core.Event;

public class EventDeserializer implements Deserializer<Event<Object>> {

    @Override
    public Event<Object> deserialize(String topic, byte[] bytes) {
        try {
            return (Event<Object>)JsonSerdes.getSingleton().deserialize(bytes, Event.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
