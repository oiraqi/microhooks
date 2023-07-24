package io.microhooks.examples.spring;

import io.microhooks.source.Projection;

import lombok.Data;

@Data
@Projection
public class Projection1 {
    private String name;

    @Override
    public String toString() {
        return "Projection1 {name: " + name + "}";
    }
}
