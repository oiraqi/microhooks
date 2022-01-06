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
import io.microhooks.eda.MappedEvent;
import io.microhooks.util.Reflector;
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

    @OnCreate
    public List<MappedEvent<Long, Object>> onCreate() {
        ArrayList<MappedEvent<Long, Object>> mappedEvents = new ArrayList<>();
        mappedEvents
                .add(new MappedEvent<>(new Event<>(1L, new TestDTO(1, "Omar"), "Label"),
                        new String[] { "test" }));
        return mappedEvents;
    }

    @OnUpdate
    public List<MappedEvent<String, String>> onUpdate(Map<String, String> changedTrackedFieldsPreviousValues)
            throws Exception {
        ArrayList<MappedEvent<String, String>> mappedEvents = new ArrayList<>();
        Iterator<String> keys = changedTrackedFieldsPreviousValues.keySet().iterator();
        while (keys.hasNext()) {
            String fieldName = keys.next();
            Object oldValue = changedTrackedFieldsPreviousValues.get(fieldName);
            mappedEvents.add(new MappedEvent<>(new Event<>(fieldName,
                    oldValue.toString() + " --> " + Reflector.getFieldValue(this, fieldName).toString(), "Update"),
                    new String[] { "test" }));
        }
        return mappedEvents;
    }

}
