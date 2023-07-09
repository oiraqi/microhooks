package io.microhooks.tests.spring.kafka;

import java.util.ArrayList;
import java.util.List;
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
@Source(mappings = {"Test:io.microhooks.test.spring.TestDTO"})
@CustomSource
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Track
    private String name;

    @OnCreate(streams = "CustomStream")
    public List<Event<String>> onCreate() {
        List<Event<String>> events = new ArrayList<>();
        events.add(new Event<>(name, "CustomCreate"));
        return events;
    }

    @OnUpdate(streams = "CustomStream")
    public List<Event<String>> onUpdate(Map<String, Object> changedTrackedFieldsPreviousValues) {
        List<Event<String>> events = new ArrayList<>();
        String oldName = (String) changedTrackedFieldsPreviousValues.get("name");
        System.out.println(oldName + " --> " + name);
        events.add(new Event<>(oldName + " --> " + name, "NameChanged"));
        return events;
    }

}
