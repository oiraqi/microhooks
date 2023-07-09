package io.microhooks.tests.spring.kafka;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationBootstrap implements ApplicationListener<ApplicationPreparedEvent> {

    public void onApplicationEvent(ApplicationPreparedEvent ev) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }
}