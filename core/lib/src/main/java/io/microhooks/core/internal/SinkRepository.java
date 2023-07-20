package io.microhooks.core.internal;

import com.fasterxml.jackson.databind.JsonNode;

public interface SinkRepository {

    void create(Object sinkEntity, long sourceId);

    void update(Class<?> sinkEntity, JsonNode payload, long sourceId) throws Exception;
    
}
