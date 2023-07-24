package io.microhooks.internal.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.common.Event;

public class EventSerdes {
    //Thread-safe, as long as we don't call setConfig and setDateFormat
    private final ObjectMapper mapper = new ObjectMapper();
    private static EventSerdes singleton = null;

    private EventSerdes() {
        //Don't let anyone instantiate me!
    }

    public static EventSerdes getSingleton() {
        if (singleton == null) {
            singleton = new EventSerdes();
        }
        return singleton;
    }

    public byte[] serialize(Event<Object> event) throws IOException {
        byte[] bytes = mapper.writeValueAsBytes(event);
        return bytes;
    }

    public Event<JsonNode> deserialize(byte[] bytes) throws IOException {
        JsonNode root = mapper.readTree(bytes);        
        String label = root.at("/label").textValue();
        JsonNode payload = root.at("/payload");
        String owner = root.at("/owner").textValue();
        long timestamp = root.at("/timestamp").asLong();
        Event<JsonNode> event = new Event<JsonNode>();
        event.setPayload(payload);
        event.setLabel(label);
        event.setOwner(owner);
        event.setTimestamp(timestamp);
        return event;
    }
}
