package io.microhooks.core;

import java.util.Date;

import lombok.Getter;

@Getter
public class Event<T> {
	private final long timestamp;
	private final T payload;
	private final String label;

	public Event(T payload, String label) {
		this.timestamp = new Date().getTime();
		this.payload = payload;
		this.label = label;
	}

}
