package io.microhooks.examples.spring.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Projection2 {
    private int amount;
}
