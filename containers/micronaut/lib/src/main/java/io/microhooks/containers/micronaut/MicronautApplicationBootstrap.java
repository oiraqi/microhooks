package io.microhooks.containers.micronaut;

import javax.inject.Singleton;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import io.microhooks.core.internal.ApplicationBootstrap;
import io.microhooks.core.internal.SinkRepository;

@Singleton
public class MicronautApplicationBootstrap extends ApplicationBootstrap {

    @Inject
    SinkRepository sinkRepository;

    @EventListener
    public void setup(StartupEvent event) throws Exception {
        System.out.println("Hi From Micronaut Bottstrap");
        setup(sinkRepository);
    }

}