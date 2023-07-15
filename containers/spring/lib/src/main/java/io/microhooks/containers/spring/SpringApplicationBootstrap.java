package io.microhooks.containers.spring;

import java.lang.reflect.Method;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.microhooks.core.internal.ApplicationBootstrap;

@Component
public class SpringApplicationBootstrap extends ApplicationBootstrap {

    @EventListener(ApplicationReadyEvent.class)
    public void setup() throws Exception {
        super.setup();
    }

}