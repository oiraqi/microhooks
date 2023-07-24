package io.microhooks.internal;

import java.util.Map;

public interface Trackable {

    //The concrete type shall be ConcurrentHashMap for thread safety with
    //minimum impact on performance
    Map<String, Object> getMicrohooksTrackedFields();
    void setMicrohooksTrackedFields(Map<String, Object> map);
}
