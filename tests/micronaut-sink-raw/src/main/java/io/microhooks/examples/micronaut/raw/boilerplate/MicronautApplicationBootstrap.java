package io.microhooks.tests.micronaut.raw.boilerplate;

import java.io.ObjectInputFilter.Config;

import javax.inject.Singleton;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;

@Singleton
public class MicronautApplicationBootstrap {

    @Inject
    SinkRepository sinkRepository;

    @EventListener
    public void setup(StartupEvent event) throws Exception {
        System.out.println("Hi From Micronaut Bottstrap");
        KafkaEventConsumer consumer = new KafkaEventConsumer("localhost:9092");
        new Thread(() -> {
                try {
                    consumer.launch(sinkRepository);
                } catch (Exception e) {
                    e.printStackTrace();
                }
           }).start();
    }

}