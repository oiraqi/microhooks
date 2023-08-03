package io.microhooks.tests.micronaut.raw.boilerplate;

import com.fasterxml.jackson.databind.JsonNode;

import io.microhooks.tests.micronaut.raw.SinkEntity;

public interface SinkRepository {

    void create(SinkEntity sinkEntity, long sourceId);

    boolean update(JsonNode payload, long sourceId);

    void delete(long sourceId);
    
}
