package io.microhooks.brokers.rabbitmq;

import java.io.IOException;

import io.microhooks.core.Event;
import io.microhooks.core.internal.EventProducer;
import io.microhooks.core.internal.util.JsonSerdes;

public class RabbitMQEventProducer extends EventProducer {

    public RabbitMQEventProducer(String brokers) {
        //To do
    }

    @Override
    protected void doPublish(long id, Event<Object> event, String stream) {
        //To do
        try {
            System.out.println("RabbitMQ: " + id + " / " + new String(JsonSerdes.getSingleton().serialize(event)) + " / " + stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
