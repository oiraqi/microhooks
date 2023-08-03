package io.microhooks.examples.spring.raw.boilerplate;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.examples.spring.raw.Projection1;
import io.microhooks.examples.spring.raw.Projection2;
import io.microhooks.examples.spring.raw.SourceEntity;

import java.util.Map;

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

    @PostUpdate
    public void onPostUpdate(SourceEntity entity) throws Exception {
        Map<String, String> previousState = entity.getPreviousState();
        if (previousState.get("name") != entity.getName()) {
            String oldName = previousState.get("name");
            String newName = entity.getName();
            getEventProducer(brokers).publish(entity.getId(), new Event<>(oldName + " --> " + newName, "NameChanged"), serviceName + "-CustomStream1");
        }
        if (previousState.get("amount") != null) {
            int oldAmount = Integer.parseInt(previousState.get("amount"));
            int newAmount = entity.getAmount();
            if (Math.abs(newAmount - oldAmount) > 5) {
                Event<Object> event = new Event<>(oldAmount + " --> " + newAmount, "AmountExcessivelyChanged");
                getEventProducer(brokers).publish(entity.getId(), event, serviceName + "-CustomStream1");
            }
        }
        getEventProducer(brokers).publish(entity.getId(), new Event<>("Hi Micronaut!", "Greetings"), "CustomStream1");
        getEventProducer(brokers).publish(entity.getId(), new Event<>("Hi Quarkus!", "Greetings"), "CustomStream2");

        previousState.put("name", entity.getName());
        previousState.put("amount", String.valueOf(entity.getAmount()));
    }
}
