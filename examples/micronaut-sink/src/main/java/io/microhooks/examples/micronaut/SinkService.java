package io.microhooks.examples.micronaut;


import com.fasterxml.jackson.databind.JsonNode;

import io.microhooks.sink.CustomSink;
import io.microhooks.sink.ProcessEvent;
import io.microhooks.common.Event;
import jakarta.inject.Singleton;

@Singleton
@CustomSink
public class SinkService {

    @ProcessEvent(stream="SourceMicroservice-CustomStream1", label="NameChanged")
    public void processNameChangedEvent(long key, Event<JsonNode> event) {
        System.out.println("----------NAME CHANGED---------------");
        System.out.println("Received Event Key: " + key);
        System.out.println("Received Event Timestamp: " + event.getTimestamp());
        // System.out.println("Received Event Username: " + event.getUsername());
        System.out.println("Received Event Payload: " + event.getPayload().asText());        
    }

    @ProcessEvent(stream="SourceMicroservice-CustomStream1", label="Greetings")
    public void processGreetingsEvents(long key, Event<JsonNode> event) {
        System.out.println("----------GREETINGS---------------");
        System.out.println("Received Event Key: " + key);
        System.out.println("Received Event Timestamp: " + event.getTimestamp());
        // System.out.println("Received Event Username: " + event.getUsername());
        System.out.println("Received Event Payload: " + event.getPayload().asText());        
    }
    
}
