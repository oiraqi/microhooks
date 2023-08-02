package io.microhooks.examples.spring.raw;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.examples.spring.raw.EntityListener;
import io.microhooks.examples.spring.raw.Projection2;
import io.microhooks.examples.spring.raw.SourceEntity;

import org.springframework.beans.factory.annotation.Value;

public class SourceEntityListener extends EntityListener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${broker.cluster}")
    String brokers;

    @PostPersist
    public void onPostPersist(SourceEntity entity) throws Exception {
        Object projection1 = objectMapper.convertValue(entity, Projection1.class);
        Event<Object> event1 = new Event<>(projection1, Event.RECORD_CREATED);
        getEventProducer(brokers).publish(entity.getId(), event1, "Stream1");
        Object projection2 = objectMapper.convertValue(entity, Projection2.class);
        Event<Object> event2 = new Event<>(projection1, Event.RECORD_CREATED);
        getEventProducer(brokers).publish(entity.getId(), event2, "Stream2");
    }

    @PostPersist
    public void onPostPersistCustom(SourceEntity entity) throws Exception{
        Event<Object> event = new Event<>(entity.getName(), "CustomCreate");
        getEventProducer(brokers).publish(entity.getId(), event, "CustomStream");
    }
}
