package io.microhooks.extensions.environments.spring;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationBootstrap implements ApplicationListener<ApplicationPreparedEvent> {

    public void onApplicationEvent(ApplicationPreparedEvent ev) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }
}