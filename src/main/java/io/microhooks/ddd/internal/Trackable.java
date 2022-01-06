package io.microhooks.ddd.internal;

import java.util.Map;

public interface Trackable {
    Map<String, Object> getTrackedFields();
}
