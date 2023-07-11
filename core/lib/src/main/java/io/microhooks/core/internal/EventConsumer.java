package io.microhooks.core.internal;

public interface EventConsumer {

    void subscribeSink(Class<?> sink, String stream);
    
}
