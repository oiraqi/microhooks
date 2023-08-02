package io.microhooks.examples.spring.raw;

import java.io.IOException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EventSerializer implements Serializer<Event<Object>> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Event<Object> event) {
        try {
            return mapper.writeValueAsBytes(event);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    
}
