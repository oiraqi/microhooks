package io.microhooks.eda.providers.rabbitmq;

import io.microhooks.eda.Event;
import io.microhooks.eda.EventProducer;

public class RabbitMQEventProducer<T, U> extends EventProducer<T, U> {

    private static final String BOOTSTRAP_SERVERS = "${io.microhooks.providers.broker.cluster}";

    public RabbitMQEventProducer() {
        //To do
    }

    @Override
    protected void publish(T key, Event<T, U> event, String stream) {
        //To do
    }
    
}
