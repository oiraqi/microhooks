package io.microhooks.ddd;

import java.util.List;

import io.microhooks.eda.MappedEvent;

public interface EventGenerator<T, U> {
    
    public List<MappedEvent<T, U>> onCreate(Object entity);

    public List<MappedEvent<T, U>> onUpdate(Object entity, TrackedFields trackedFields);

    public List<MappedEvent<T, U>> onDelete(Object entity);
}
