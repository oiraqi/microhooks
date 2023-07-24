package io.microhooks.brokers.rabbitmq;

import java.io.IOException;

import io.microhooks.common.Event;
import io.microhooks.internal.EventProducer;
import io.microhooks.internal.util.EventSerdes;

public class RabbitMQEventProducer extends EventProducer {

    public RabbitMQEventProducer(String brokers) {
        //To do
    }

    @Override
    protected void doPublish(long id, Event<Object> event, String stream) {
        //To do
        try {
            System.out.println("RabbitMQ: " + id + " / " + new String(EventSerdes.getSingleton().serialize(event)) + " / " + stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
