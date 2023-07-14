package io.microhooks.core.internal;

import java.io.IOException;

import io.microhooks.core.Event;
import io.microhooks.core.internal.util.JsonSerdes;

public class NullEventProducer extends EventProducer {

    @Override
    public void publish(long id, Event<Object> event, String stream) {
        try {
            System.out.println(JsonSerdes.getSingleton().serialize(id) + " " + new String(JsonSerdes.getSingleton().serialize(event)) + " " + stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
