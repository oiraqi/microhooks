package io.microhooks.containers.micronaut;

import javax.inject.Singleton;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import javax.inject.Inject;
import io.microhooks.core.internal.ApplicationBootstrap;
import io.microhooks.core.internal.EventRepository;

@Singleton
public class MicronautApplicationBootstrap extends ApplicationBootstrap {

    @Inject
    EventRepository eventRepository;

    @EventListener
    public void setup(StartupEvent event) throws Exception {
        System.out.println("Hi From Micronaut Bottstrap");
        setup(eventRepository);
    }

}