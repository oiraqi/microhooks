package io.microhooks.examples.spring.raw.boilerplate;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.examples.spring.raw.Projection1;
import io.microhooks.examples.spring.raw.Projection2;
import io.microhooks.examples.spring.raw.SourceEntity;

import org.springframework.beans.factory.annotation.Value;

public class CustomSourceEntityListener extends EntityListener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${broker.cluster}")
    String brokers;

    @Value("${service.name}")
    String serviceName;

    @PostPersist
    public void onPostPersist(SourceEntity entity) throws Exception{
        Event<Object> event = new Event<>(entity.getName(), "CustomCreate");
        getEventProducer(brokers).publish(entity.getId(), event, serviceName + "-CustomStream");
    }
}
