package io.microhooks.core.internal;

import java.util.Map;
import java.util.Vector;

public interface Trackable {    
    //This is a static field, so shared among all Trackable entities of same class
    //We use Vector here for thread safety
    //A cache for reflected fields so that reflection is performed only
    //once per Trackable entity class (for all its instances)
    Vector<String> MICROHOOKS_TRACKED_FIELDS_NAMES = new Vector<>();

    //The concrete type shall be ConcurrentHashMap for thread safety with
    //minimum impact on performance
    Map<String, Object> getMicrohooksTrackedFields();
    void setMicrohooksTrackedFields(Map<String, Object> map);
}
