package io.microhooks.tests.micronaut.raw.boilerplate;

import java.util.List;
import java.util.Set;

import io.microhooks.tests.micronaut.raw.SinkService;

import java.util.HashSet;

public class CustomSinks {
    private static Set<SinkService> set = new HashSet<>();

    public static void register(SinkService sinkService) {
        set.add(sinkService);
    }

    public static Set<SinkService> get() {
        return set;
    }
}
