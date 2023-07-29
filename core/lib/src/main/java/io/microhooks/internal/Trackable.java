package io.microhooks.internal;

import java.util.Map;

public interface Trackable {
    //The concrete type shall be ConcurrentHashMap for thread safety with
    //minimum impact on performance
    Map<String, String> getMicrohooksTrackedFields();
    void setMicrohooksTrackedFields(Map<String, String> map);
}
