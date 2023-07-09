package io.microhooks.internal;

import java.util.Map;

public interface Trackable {
    Map<String, Object> getMicrohooksTrackedFields();
    void setMicrohooksTrackedFields(Map<String, Object> map);
}
