package io.microhooks.eda;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MappedEvent<T, U> {
    
    private Event<T, U> event;

    @Getter
    private String[] streams;

    public T getKey() {
        return event.getKey();
    }

    public U getPayload() {
        return event.getPayload();
    }

    public String getLabel() {
        return event.getLabel();
    }
}
