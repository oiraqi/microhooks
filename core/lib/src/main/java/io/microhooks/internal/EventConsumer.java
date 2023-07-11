package io.microhooks.internal;

public interface EventConsumer {

    void subscribeSink(Class<?> sink, String stream);
    
}
