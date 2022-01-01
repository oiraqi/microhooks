package io.microhooks.eda;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MappedEvent<T, U> {
    
    private Event<T, U> event;
    private String[] streams;
}
