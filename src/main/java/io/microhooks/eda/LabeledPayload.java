package io.microhooks.eda;

import lombok.Value;

@Value
public class LabeledPayload<T> {
    private final T payload;
	private final String label;
}
