package io.microhooks.extensions.environments.spring;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class EventSubscriptionSetup implements ApplicationListener<ApplicationReadyEvent> {

    public void onApplicationEvent(ApplicationReadyEvent are) {
        System.out.println("xxxxxxxxxxxxHELLOxxxxxxxxxxxxxxx");
    }
}