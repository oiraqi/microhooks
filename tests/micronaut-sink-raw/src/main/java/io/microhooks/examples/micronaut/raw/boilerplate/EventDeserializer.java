package io.microhooks.tests.micronaut.raw.boilerplate;

import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventDeserializer implements Deserializer<Event<JsonNode>> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Event<JsonNode> deserialize(String topic, byte[] bytes) {
        try {
            return deserialize(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Event<JsonNode> deserialize(byte[] bytes) throws IOException {
        JsonNode root = mapper.readTree(bytes);        
        String label = root.at("/label").textValue();
        JsonNode payload = root.at("/payload");
        long timestamp = root.at("/timestamp").asLong();
        Event<JsonNode> event = new Event<JsonNode>();
        event.setPayload(payload);
        event.setLabel(label);
        event.setTimestamp(timestamp);
        return event;
    }
    
}
