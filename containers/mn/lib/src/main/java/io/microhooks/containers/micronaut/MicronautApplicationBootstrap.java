package io.microhooks.containers.micronaut;

import javax.inject.Singleton;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;


import io.microhooks.core.internal.ApplicationBootstrap;

@Singleton
public class MicronautApplicationBootstrap extends ApplicationBootstrap {

    @EventListener
    public void setup(StartupEvent event) throws Exception {
        System.out.println("Hi From Micronaut Bottstrap");
        super.setup();
    }

}