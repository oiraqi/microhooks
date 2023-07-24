package io.microhooks.examples.spring;

import io.microhooks.common.Dto;

import lombok.Data;

@Data
@Dto
public class Dto1 {
    private String name;

    @Override
    public String toString() {
        return "Dto1 {name: " + name + "}";
    }
}
