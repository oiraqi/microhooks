package io.microhooks.ddd.internal;

import java.util.HashMap;
import java.util.Map;

public class TrackedFields {

    private Map<String, Map<String, Object>> fields = new HashMap<>();

    public void put(String entityId, String fieldName, Object value) {
        Map<String, Object> entityTrackedFields = fields.computeIfAbsent(entityId, id -> new HashMap<>());
        entityTrackedFields.put(fieldName, value);
    }

    public Object get(String entityId, String fieldName) {
        Map<String, Object> entityTrackedFields = fields.get(entityId);
        if (entityTrackedFields == null) {
            return null;
        }
        return entityTrackedFields.get(fieldName);
    }
}
