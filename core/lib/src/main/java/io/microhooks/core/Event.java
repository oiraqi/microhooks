package io.microhooks.core;

import java.util.Date;

import io.microhooks.core.internal.util.Config;

import lombok.Getter;

@Getter
public class Event<U> {
	@Getter
	private final U payload;
	private final String label;
	private final long timestamp;
	private final String owner;

	public Event(U payload, String label) {		
		this.payload = payload;
		this.label = label;
		timestamp = new Date().getTime();
		owner = Config.getSecurityContext().getUsername();
	}

}
