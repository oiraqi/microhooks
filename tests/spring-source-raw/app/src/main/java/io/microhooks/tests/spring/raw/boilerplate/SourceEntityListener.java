package io.microhooks.tests.spring.raw.boilerplate;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.tests.spring.raw.Projection1;
import io.microhooks.tests.spring.raw.Projection2;
import io.microhooks.tests.spring.raw.SourceEntity;

import org.springframework.beans.factory.annotation.Value;

public class SourceEntityListener extends EntityListener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${broker.cluster}")
    String brokers;

    @Value("${service.name}")
    String serviceName;

    @PostPersist
    public void onPostPersist(SourceEntity entity) throws Exception {
        Object projection1 = objectMapper.convertValue(entity, Projection1.class);
        Event<Object> event1 = new Event<>(projection1, Event.RECORD_CREATED);
        getEventProducer(brokers).publish(entity.getId(), event1, "Stream1");
        Object projection2 = objectMapper.convertValue(entity, Projection2.class);
        Event<Object> event2 = new Event<>(projection1, Event.RECORD_CREATED);
        getEventProducer(brokers).publish(entity.getId(), event2, serviceName + "-Stream2");
    }

}
