package io.microhooks.internal;

import io.microhooks.internal.util.Config;

public abstract class Listener {
    
    private EventProducer eventProducer;

    protected EventProducer getEventProducer() {

        if (eventProducer == null) {
            try {
                eventProducer = Config.getEventProducer();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Returning null");
            }
            
        }
        return eventProducer;        
    }
}
