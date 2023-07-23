package io.microhooks.examples.micronaut;


import com.fasterxml.jackson.databind.JsonNode;

import io.microhooks.sink.CustomSink;
import io.microhooks.sink.ProcessEvent;
import io.microhooks.core.Event;
import jakarta.inject.Singleton;

@Singleton
@CustomSink
public class SinkService {

    @ProcessEvent(stream="SourceMicroservice-CustomStream1", label="NameChanged")
    public void processEvent(long key, Event<JsonNode> event) {
        System.out.println("----------CUSTOM---------------");
        System.out.println("Received Event Key: " + key);
        System.out.println("Received Event Timestamp: " + event.getTimestamp());
        // System.out.println("Received Event Username: " + event.getUsername());
        System.out.println("Received Event Payload: " + event.getPayload().asText());        
    }
    
}
