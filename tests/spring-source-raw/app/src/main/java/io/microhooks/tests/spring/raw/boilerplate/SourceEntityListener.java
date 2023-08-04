package io.microhooks.tests.spring.raw.boilerplate;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.tests.spring.raw.Projection1;
import io.microhooks.tests.spring.raw.Projection2;
import io.microhooks.tests.spring.raw.SourceEntity;
import io.microhooks.tests.spring.raw.monitor.Monitor;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

public class SourceEntityListener extends EntityListener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${broker.cluster}")
    String brokers;

    @Value("${service.name}")
    String serviceName;

    @PostPersist
    public void onPostPersist(SourceEntity entity) throws Exception {
        long startTime = System.nanoTime();

        Object projection1 = objectMapper.convertValue(entity, Projection1.class);
        Event<Object> event1 = new Event<>(projection1, Event.RECORD_CREATED);
        getEventProducer(brokers).publish(entity.getId(), event1, serviceName + "-Stream1");
        Object projection2 = objectMapper.convertValue(entity, Projection2.class);
        Event<Object> event2 = new Event<>(projection1, Event.RECORD_CREATED);
        getEventProducer(brokers).publish(entity.getId(), event2, serviceName + "-Stream2");

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        Monitor.sourceTotalTime += duration;

        if (duration < Monitor.sourceMinTime) {
            Monitor.sourceMinTime = duration;
        }
        if (duration > Monitor.sourceMaxTime) {
            Monitor.sourceMaxTime = duration;
        }

        Monitor.sourceCount++;

        if (Monitor.sourceCount % 1000 == 0) {
            System.out.println("min: " + Monitor.sourceMinTime);
            System.out.println("max: " + Monitor.sourceMaxTime);
            System.out.println("avg: " + (float)Monitor.sourceTotalTime / Monitor.sourceCount);
        }
    }

    @PostRemove
    public void onPostRemove(SourceEntity entity) throws Exception {
        Event<Object> event = new Event<>("", Event.RECORD_DELETED);
        getEventProducer(brokers).publish(entity.getId(), event, serviceName + "-Stream1");
        getEventProducer(brokers).publish(entity.getId(), event, serviceName + "-Stream2");
    }

}
