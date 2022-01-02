package io.microhooks;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import io.microhooks.ddd.Source;
import io.microhooks.ddd.OnCreate;
import io.microhooks.ddd.OnUpdate;
import io.microhooks.ddd.TrackedFields;
import io.microhooks.eda.Event;
import io.microhooks.eda.MappedEvent;

import lombok.Data;

@Entity
@Data
@Source
public class TestEntity {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @OnCreate
    public List<MappedEvent<Object, Object>> onCreate() {
        ArrayList<MappedEvent<Object, Object>> mappedEvents = new ArrayList<>();
        mappedEvents
                .add(new MappedEvent<>(new Event<>(1L, new TestDTO(1, "Omar"), "Label"),
                        new String[] { "test" }));
        return mappedEvents;
    }

    @OnUpdate
    public List<MappedEvent<Long, TestDTO>> onUpdate(TrackedFields trackedFields) {
        return new ArrayList<>();
    }

}
