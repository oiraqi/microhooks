package io.microhooks.internal.util;

import com.fasterxml.jackson.databind.JsonNode;

import io.microhooks.common.Event;

public class Security {
    public static boolean verify(long sourceId, Event<JsonNode> event, String authenticationKey) {
        return true;
    }
}