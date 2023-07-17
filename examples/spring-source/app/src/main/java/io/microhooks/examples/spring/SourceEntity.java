package io.microhooks.examples.spring;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.microhooks.core.Event;
import io.microhooks.producer.CustomSource;
import io.microhooks.producer.ProduceEventOnCreate;
import io.microhooks.producer.ProduceEventOnUpdate;
import io.microhooks.producer.ProduceEventsOnUpdate;
import io.microhooks.producer.Source;
import io.microhooks.producer.Track;
import lombok.Data;

@Entity
@Data
@Source(mappings = {"Stream1:io.microhooks.examples.spring.Dto1", "Stream2:io.microhooks.examples.spring.Dto2"})
@CustomSource
public class SourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Track
    private String name;

    @Track
    private int amount;

    @ProduceEventOnCreate(stream = "CustomStream")
    public Event<String> onCreate() {
        return new Event<>(name, "CustomCreate");
    }

    @ProduceEventOnUpdate(stream = "CustomStream1") // Notice Event (of ProduceEventOnUpdate) in singular form
    public Event<String> produceNameChangedEvent(Map<String, Object> changedTrackedFieldsWithPreviousValues) {
        if (!changedTrackedFieldsWithPreviousValues.containsKey("name")) {
            return null;
        }

        String oldName = (String) changedTrackedFieldsWithPreviousValues.get("name");
        System.out.println(oldName + " --> " + name);
        return new Event<>(oldName + " --> " + name, "NameChanged");
    }

    @ProduceEventOnUpdate(stream = "CustomStream2") // Notice Event (of ProduceEventOnUpdate) in singular form
    public Event<String> produceAmountExcessivelyChangedEvent(Map<String, Object> changedTrackedFieldsWithPreviousValues) {
        if (!changedTrackedFieldsWithPreviousValues.containsKey("amount")) {
            // Won't produce any event
            return null;
        }

        int oldAmount = (int) changedTrackedFieldsWithPreviousValues.get("amount");
        System.out.println(oldAmount + " --> " + amount);

        if (Math.abs(oldAmount - amount) < 5) {
            // Won't produce any event
            return null;
        }
        
        return new Event<>(oldAmount + " --> " + amount, "AmountExcessivelyChanged");
    }

    @ProduceEventsOnUpdate // Notice Events (of ProduceEventsOnUpdate) in plural form
    public Map<String, Event<String>> produceEventsOnUpdate(Map<String, Object> changedTrackedFieldsWithPreviousValues) {
        Map<String, Event<String>> streamedEvents = new HashMap<>();
        streamedEvents.put("CustomStream1", new Event<>("Hi Micronaut!", "Greetings"));
        streamedEvents.put("CustomStream2", new Event<>("Hi Quarkus!", "Greetings"));
        return streamedEvents;
    }

}
