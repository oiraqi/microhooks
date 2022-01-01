package io.microhooks.eda;

import lombok.Data;

@Data
public class MappedEvent<T, U> {
    
    private Event<T, U> evnet;
    private String[] streams;
}
