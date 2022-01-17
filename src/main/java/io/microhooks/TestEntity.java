package io.microhooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.microhooks.ddd.Source;
import io.microhooks.ddd.Track;
import io.microhooks.ddd.internal.Trackable;
import io.microhooks.ddd.OnCreate;
import io.microhooks.ddd.OnUpdate;
import io.microhooks.eda.Event;
import lombok.Data;

@Entity
@Data
@Source
public class TestEntity implements Trackable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Track
    private String name;

    private transient Map<String, Object> trackedFields = new HashMap<>();

    @OnCreate(streams = "CustomStream")
    public List<Event<Long, Object>> onCreate() {
        ArrayList<Event<Long, Object>> events = new ArrayList<>();
        events.add(new Event<>(id, new TestDTO(1, "Omar"), "CustomCreate"));
        return events;
    }

    @OnUpdate(streams = "CustomStream")
    public List<Event<Long, String>> onUpdate(Map<String, Object> changedTrackedFieldsPreviousValues) {
        ArrayList<Event<Long, String>> events = new ArrayList<>();
        String oldName = (String) changedTrackedFieldsPreviousValues.get("name");
        events.add(new Event<>(id, oldName + " --> " + name, "NameChanged"));
        return events;
    }

}
