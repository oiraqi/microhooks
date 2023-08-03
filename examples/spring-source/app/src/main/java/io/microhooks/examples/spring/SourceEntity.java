package io.microhooks.examples.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import io.microhooks.common.Event;
import io.microhooks.source.CustomSource;
import io.microhooks.source.ProduceEventOnCreate;
import io.microhooks.source.ProduceEventOnUpdate;
import io.microhooks.source.ProduceEventsOnUpdate;
import io.microhooks.source.Source;
import io.microhooks.source.Track;
import lombok.Data;

@Entity
@Data
@Source(mappings = {"Stream1:io.microhooks.examples.spring.Projection1", "Stream2:io.microhooks.examples.spring.Projection2"})
@CustomSource
public class SourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Track
    private String name;

    @Track
    private int amount;

    @ProduceEventOnCreate(streams="CustomStream")
    public Event<String> onCreate() {
        return new Event<>(name, "CustomCreate");
    }

    @ProduceEventOnUpdate(streams="CustomStream1") // Notice Event (of ProduceEventOnUpdate) in singular form
    public Event<String> produceNameChangedEvent(Map<String, Object> changedTrackedFieldsWithPreviousValues) {
        if (!changedTrackedFieldsWithPreviousValues.containsKey("name")) {
            return null;
        }
        String oldName = (String) changedTrackedFieldsWithPreviousValues.get("name");
        return new Event<>(oldName + " --> " + name, "NameChanged");
    }

    @ProduceEventOnUpdate(streams="CustomStream2") // Notice Event (of ProduceEventOnUpdate) in singular form
    public Event<String> produceAmountExcessivelyChangedEvent(Map<String, String> changedTrackedFieldsWithPreviousValues) {
        if (!changedTrackedFieldsWithPreviousValues.containsKey("amount")) {
            // Won't produce any event
            return null;
        }

        int oldAmount = Integer.parseInt(changedTrackedFieldsWithPreviousValues.get("amount"));

        if (Math.abs(oldAmount - amount) < 5) {
            // Won't produce any event
            return null;
        }
        
        return new Event<>(oldAmount + " --> " + amount, "AmountExcessivelyChanged");
    }

    @ProduceEventsOnUpdate // Notice Events (of ProduceEventsOnUpdate) in plural form
    public Map<Event<String>, String[]> produceGreetingsOnUpdate(Map<String, Object> changedTrackedFieldsWithPreviousValues) {
        Map<Event<String>, String[]> streamedEvents = new HashMap<>();
        streamedEvents.put(new Event<>("Hi Micronaut!", "Greetings"), new String[]{"CustomStream1"});
        streamedEvents.put(new Event<>("Hi Quarkus!", "Greetings"), new String[]{"CustomStream2"});
        return streamedEvents;
    }

}
