package io.microhooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.microhooks.ddd.Source;
import io.microhooks.ddd.Track;
import io.microhooks.ddd.OnCreate;
import io.microhooks.ddd.OnUpdate;
import io.microhooks.eda.Event;
import io.microhooks.eda.MappedEvent;

import lombok.Data;

@Entity
@Data
@Source
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Track
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
    public List<MappedEvent<Long, TestDTO>> onUpdate(Map<String, Object> changedTrackedFieldsPreviousValues) {
        return new ArrayList<>();
    }

}
