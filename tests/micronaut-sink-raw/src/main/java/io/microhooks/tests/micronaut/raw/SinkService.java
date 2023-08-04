package io.microhooks.tests.micronaut.raw;


import com.fasterxml.jackson.databind.JsonNode;

import io.microhooks.tests.micronaut.raw.boilerplate.Event;
import io.microhooks.tests.micronaut.raw.boilerplate.CustomSinks;

import jakarta.inject.Singleton;

@Singleton
public class SinkService {

    //Boilerplate code
    public SinkService() {
        CustomSinks.register(this);
    }

    //@ProcessEvent(stream="SourceMicroservice-CustomStream1", label="NameChanged")
    public void processNameChangedEvent(long key, Event<JsonNode> event) {
        System.out.println("----------NAME CHANGED---------------");
        System.out.println("Received Event Key: " + key);
        System.out.println("Received Event Timestamp: " + event.getTimestamp());
        System.out.println("Received Event Payload: " + event.getPayload().asText());        
    }

    //@ProcessEvent(stream="SourceMicroservice-CustomStream1", label="Greetings")
    public void processGreetingsEvents(long key, Event<JsonNode> event) {
        System.out.println("----------GREETINGS---------------");
        System.out.println("Received Event Key: " + key);
        System.out.println("Received Event Timestamp: " + event.getTimestamp());
        System.out.println("Received Event Payload: " + event.getPayload().asText());        
    }
    
}
