package io.microhooks.core.internal;

import io.microhooks.core.internal.util.CachingReflector;
import io.microhooks.core.internal.util.Config;

public class ApplicationBootstrap {

    // Callback to be exposed to the underlying container (Spring, Quarkus,
    // Micronaut, ...)
    // by the overriding container extension
    public void setup(SinkRepository sinkRepository) {

        Config.init();
        
        if (!CachingReflector.getSinkMap().isEmpty()) {
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
