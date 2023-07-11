package io.microhooks.core.internal;

import io.microhooks.core.internal.util.Config;

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
