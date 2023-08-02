package io.microhooks.examples.spring.raw;


public abstract class EntityListener {
    
    private KafkaEventProducer eventProducer;
    
    protected KafkaEventProducer getEventProducer(String brokers) {

        if (eventProducer == null) {
            try {
                eventProducer = new KafkaEventProducer(brokers);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        return eventProducer;        
    }
}
