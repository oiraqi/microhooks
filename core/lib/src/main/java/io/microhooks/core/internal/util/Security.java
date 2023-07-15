package io.microhooks.core.internal.util;

import io.microhooks.core.Event;

public class Security {
    public static boolean verify(long sourceId, Event<Object> event, String authenticationKey) {
        return true;
    }
}
