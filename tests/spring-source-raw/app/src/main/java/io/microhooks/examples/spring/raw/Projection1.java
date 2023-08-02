package io.microhooks.examples.spring.raw;

import lombok.Data;

@Data
public class Projection1 {
    private String name;

    @Override
    public String toString() {
        return "Projection1 {name: " + name + "}";
    }
}
