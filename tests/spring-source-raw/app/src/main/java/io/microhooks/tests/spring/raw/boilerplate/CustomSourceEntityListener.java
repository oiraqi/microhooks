package io.microhooks.tests.spring.raw.boilerplate;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.tests.spring.raw.Projection1;
import io.microhooks.tests.spring.raw.Projection2;
import io.microhooks.tests.spring.raw.SourceEntity;

import io.microhooks.tests.spring.raw.monitor.Monitor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

public class CustomSourceEntityListener extends EntityListener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${broker.cluster}")
    String brokers;

    @Value("${service.name}")
    String serviceName;

    @PostPersist
    public void onPostPersist(SourceEntity entity) throws Exception {
        Event<Object> event = new Event<>(entity.getName(), "CustomCreate");
        getEventProducer(brokers).publish(entity.getId(), event, serviceName + "-CustomStream");
        Map<String, String> previousState = new ConcurrentHashMap<>();
        previousState.put("name", entity.getName());
        previousState.put("amount", String.valueOf(entity.getAmount()));
        entity.setPreviousState(previousState);
    }

    @PostLoad
    //@Logged
    public void onPostLoad(SourceEntity entity) throws Exception {
        Map<String, String> previousState = new ConcurrentHashMap<>();
        previousState.put("name", entity.getName());
        previousState.put("amount", String.valueOf(entity.getAmount()));
        entity.setPreviousState(previousState);
    }

    @PostUpdate
    public void onPostUpdate(SourceEntity entity) throws Exception {
        long startTime = System.nanoTime();

        Map<String, String> previousState = entity.getPreviousState();
        String oldName = previousState.get("name");
        String newName = entity.getName();
        if (oldName == null || !oldName.equals(newName)) {            
            previousState.put("name", newName);
            getEventProducer(brokers).publish(entity.getId(), new Event<>(oldName + " --> " + newName, "NameChanged"), serviceName + "-CustomStream1");
        }
        if (previousState.get("amount") != null) {
            int oldAmount = Integer.parseInt(previousState.get("amount"));
            int newAmount = entity.getAmount();
            previousState.put("amount", String.valueOf(newAmount));
            if (Math.abs(newAmount - oldAmount) > 5) {
                Event<Object> event = new Event<>(oldAmount + " --> " + newAmount, "AmountExcessivelyChanged");
                getEventProducer(brokers).publish(entity.getId(), event, serviceName + "-CustomStream2");
            }
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        Monitor.customTotalTime += duration;

        Monitor.customCount++;

        if (Monitor.customCount % 1000 == 0) {
            System.out.println("duration: " + duration);
            System.out.println("custom count: " + Monitor.customCount);
            System.out.println("custom total: " + Monitor.customTotalTime);
            System.out.println("custom avg: " + (float)Monitor.customTotalTime / Monitor.customCount);
        }
        /*getEventProducer(brokers).publish(entity.getId(), new Event<>("Hi Micronaut!", "Greetings"), serviceName + "-CustomStream1");
        getEventProducer(brokers).publish(entity.getId(), new Event<>("Hi Quarkus!", "Greetings"), serviceName + "-CustomStream2");*/

    }
}
