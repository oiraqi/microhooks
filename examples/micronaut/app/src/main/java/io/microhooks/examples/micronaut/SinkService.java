package io.microhooks.examples.micronaut;


import io.microhooks.consumer.CustomSink;
import io.microhooks.consumer.ProcessEvent;
import io.microhooks.core.Event;

@CustomSink
public class SinkService {

    @ProcessEvent(stream="CustomStream", label="NameChanged")
    public void processEvent(long key, Event<String> event) {
        System.out.println("Received Event Key: " + key);
        System.out.println("Received Event Timestamp: " + event.getTimestamp());
        // System.out.println("Received Event Username: " + event.getUsername());
        System.out.println("Received Event Payload: " + event.getPayload());        
    }
    
}
