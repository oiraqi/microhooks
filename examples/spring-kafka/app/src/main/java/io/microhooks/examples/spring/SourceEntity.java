package io.microhooks.examples.spring;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.microhooks.core.Event;
import io.microhooks.producer.CustomSource;
import io.microhooks.producer.OnCreate;
import io.microhooks.producer.OnUpdate;
import io.microhooks.producer.Source;
import io.microhooks.producer.Track;
import lombok.Data;

@Entity
@Data
@Source(mappings = {"Stream1:io.microhooks.examples.spring.kafka.Dto1", "Stream2:io.microhooks.examples.spring.kafka.Dto2"})
@CustomSource
public class SourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Track
    private String name;

    @OnCreate(stream = "CustomStream")
    public Event<String> onCreate() {
        return new Event<>(name, "CustomCreate");
    }

    @OnUpdate(stream = "CustomStream")
    public Event<String> onUpdate(Map<String, Object> changedTrackedFieldsPreviousValues) {
        String oldName = (String) changedTrackedFieldsPreviousValues.get("name");
        System.out.println(oldName + " --> " + name);
        return new Event<>(oldName + " --> " + name, "NameChanged");
    }

}
