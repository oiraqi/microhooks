package io.microhooks.examples.spring.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Projection1 {
    private String name;

    @Override
    public String toString() {
        return "Projection1 {name: " + name + "}";
    }
}
