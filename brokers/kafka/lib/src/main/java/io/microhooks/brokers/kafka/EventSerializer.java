package io.microhooks.brokers.kafka;

import java.io.IOException;
import org.apache.kafka.common.serialization.Serializer;

import io.microhooks.internal.util.EventSerdes;
import io.microhooks.common.Event;

public class EventSerializer implements Serializer<Event<Object>> {

    @Override
    public byte[] serialize(String topic, Event<Object> event) {
        try {
            return EventSerdes.getSingleton().serialize(event);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    
}
