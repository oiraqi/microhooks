package io.microhooks.extensions.brokers.rabbitmq;

import java.io.IOException;

import io.microhooks.core.Event;
import io.microhooks.internal.EventProducer;
import io.microhooks.internal.util.JsonSerdes;

public class RabbitMQEventProducer extends EventProducer {

    public RabbitMQEventProducer(String brokers) {
        //To do
    }

    @Override
    protected void publish(String key, Event<Object> event, String stream) {
        //To do
        try {
            System.out.println("RabbitMQ: " + key + " / " + new String(JsonSerdes.getSingleton().serialize(event)) + " / " + stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
