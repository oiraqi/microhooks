package io.microhooks.containers.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.microhooks.core.internal.ApplicationBootstrap;
import io.microhooks.core.internal.SinkRepository;

@Component
public class SpringApplicationBootstrap extends ApplicationBootstrap {

    @Autowired
    SinkRepository sinkRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void setup() {
        super.setup(sinkRepository);
    }

}