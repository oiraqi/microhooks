package io.microhooks.common;

import java.util.Date;

import lombok.Getter;

@Getter
public class Event<P> {
	public static final String RECORD_CREATED = "_R_C_";
	public static final String RECORD_UPDATED = "_R_U_";
	public static final String RECORD_DELETED = "_R_D_";

	private P payload = null;
	private String label = null;
	private long timestamp = 0;

	public Event() {
	}

	public Event(P payload, String label) {
		this.payload = payload;
		this.label = label;
		timestamp = new Date().getTime();
	}

	// An event must be immutable
	public void setPayload(P payload) {
		if (this.payload == null) {
			this.payload = payload;
		}
	}

	public void setLabel(String label) {
		if (this.label == null) {
			this.label = label;
		}
	}

	public void setTimestamp(long timestamp) {
		if (this.timestamp == 0) {
			this.timestamp = timestamp;
		}
	}

	@Override
	public String toString() {
		return "{Label: " + label + ", Timestamp: " + timestamp + ", Payload: " + payload + "}";
	}

}
