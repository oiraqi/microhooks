package io.microhooks.internal;


import io.microhooks.internal.util.Config;

public abstract class EntityListener {
    
    private EventProducer eventProducer;
    
    protected EventProducer getEventProducer() {

        if (eventProducer == null) {
            try {
                eventProducer = Config.getEventProducer();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        return eventProducer;        
    }
}
