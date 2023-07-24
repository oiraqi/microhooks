package io.microhooks.internal;

import io.microhooks.internal.util.CachingReflector;
import io.microhooks.internal.util.Config;

public class ApplicationBootstrap {

    // Callback to be exposed to the underlying container (Spring, Quarkus, Micronaut, ...)
    // By the overriding container extension
    public void setup(SinkRepository sinkRepository) {

        Config.init();
        CachingReflector.init();
        System.out.println(Config.getServiceName() + " Started!");
        
        if (CachingReflector.hasSinks()) {
            new Thread(() -> {
                try {
                    Config.getEventConsumer().launch(sinkRepository);
                } catch (Exception e) {
                    e.printStackTrace();
                }
           }).start();
        }
    }
}
