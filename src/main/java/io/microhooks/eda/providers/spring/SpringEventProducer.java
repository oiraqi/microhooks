package io.microhooks.eda.providers.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import io.microhooks.eda.Event;
import io.microhooks.eda.EventProducer;
import io.microhooks.util.logging.Logged;

public class SpringEventProducer<T, U> extends EventProducer<T, U> {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Logged
    public void publish(Event<T, U> event, String stream) {
        applicationEventPublisher.publishEvent(event);
    }
    
}
