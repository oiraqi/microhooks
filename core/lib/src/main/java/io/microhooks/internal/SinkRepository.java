package io.microhooks.internal;

import com.fasterxml.jackson.databind.JsonNode;

public interface SinkRepository {

    void create(Object sinkEntity, long sourceId);

    boolean update(Class<?> sinkEntity, JsonNode payload, long sourceId) throws Exception;

    void delete(Class<?> sinkEntity, long sourceId);
    
}
