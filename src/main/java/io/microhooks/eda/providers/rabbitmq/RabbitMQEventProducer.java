package io.microhooks.eda.providers.rabbitmq;

import java.io.IOException;

import io.microhooks.eda.Event;
import io.microhooks.eda.EventProducer;
import io.microhooks.util.JsonSerdes;

public class RabbitMQEventProducer<T, U> extends EventProducer<T, U> {

    public RabbitMQEventProducer(String brokers) {
        //To do
    }

    @Override
    protected void publish(T key, Event<T, U> event, String stream) {
        //To do
        try {
            System.out.println("RabbitMQ: " + key + " / " + new String(JsonSerdes.getSingleton().serialize(event)) + " / " + stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
