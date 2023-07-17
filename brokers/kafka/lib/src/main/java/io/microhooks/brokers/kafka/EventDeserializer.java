package io.microhooks.brokers.kafka;

import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.JsonNode;

import io.microhooks.core.internal.util.EventSerdes;
import io.microhooks.core.Event;

public class EventDeserializer implements Deserializer<Event<JsonNode>> {

    @Override
    public Event<JsonNode> deserialize(String topic, byte[] bytes) {
        try {
            return EventSerdes.getSingleton().deserialize(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
