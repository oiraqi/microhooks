package io.microhooks.ddd;

import java.util.ArrayList;
import java.util.List;

import io.microhooks.eda.MappedEvent;

public class NullEventGenerator<T, U> implements EventGenerator<T, U> {
    public List<MappedEvent<T, U>> onCreate(Object entity) {
        return new ArrayList<>();
    }

    public List<MappedEvent<T, U>> onUpdate(Object entity, TrackedFields trackedFields) {
        return new ArrayList<>();
    }

    public List<MappedEvent<T, U>> onDelete(Object entity) {
        return new ArrayList<>();
    }
}
