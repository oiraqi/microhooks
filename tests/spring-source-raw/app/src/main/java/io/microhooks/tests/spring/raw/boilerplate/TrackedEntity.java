package io.microhooks.tests.spring.raw.boilerplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

@Data
public abstract class TrackedEntity {

    private transient Map<String, String> previousState = new ConcurrentHashMap<>();

}
