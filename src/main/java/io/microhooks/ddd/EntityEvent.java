package io.microhooks.ddd;

import io.microhooks.eda.Event;

public class EntityEvent<T, U> extends Event<T, U> {
    private static final String CREATED = "C";
    private static final String UPDATED = "U";
    private static final String DELETED = "D";

    public EntityEvent(T key, U payload, String label) {
        super(key, payload, label);
        if (label == null
                || (!label.equals(CREATED) &&
                        !label.equals(UPDATED) &&
                        !label.equals(DELETED))) {
            throw new IllegalArgumentException(label);
        }
    }
}
