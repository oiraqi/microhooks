package io.microhooks.examples.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.microhooks.consumer.CustomSink;
import io.microhooks.consumer.ProcessEvent;
import io.microhooks.core.Event;

@CustomSink
@Service
public class SinkService {

    @Autowired
    SourceRepository repo;

    @ProcessEvent(stream="CustomStream", label="NameChanged")
    public void processEvent(long key, Event<String> event) {
        System.out.println("Received Event Key: " + key);
        System.out.println("Received Event Timestamp: " + event.getTimestamp());
        // System.out.println("Received Event Username: " + event.getUsername());
        System.out.println("Received Event Payload: " + event.getPayload());        
    }
    
}
