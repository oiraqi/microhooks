package io.microhooks.util;

public class Constants {
    private Constants() {}
    
    public static final String TRACKED_FIELDS_PREVIOUS_VALUES = "microhooksTrackedFieldsPreviousValues";
    public static final String TRACKED_FIELDS_PREVIOUS_VALUES_GETTER = "get" + Character.toUpperCase(TRACKED_FIELDS_PREVIOUS_VALUES.charAt(0))
                                                + TRACKED_FIELDS_PREVIOUS_VALUES.substring(1);
}
