package io.microhooks.core.internal;

public interface SinkRepository {

    void create(Object sinkEntity, long sourceId);

    void update(Class<?> sinkEntity, Object payload, long sourceId) throws Exception;
    
}
