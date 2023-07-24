package io.microhooks.common;

import java.util.Date;

import io.microhooks.internal.util.Config;
import lombok.Getter;

@Getter
public class Event<P> {
	public static final String RECORD_CREATED = "_R_C_";
    public static final String RECORD_UPDATED = "_R_U_";
    public static final String RECORD_DELETED = "_R_D_";

	private P payload = null;
	private String label = null;
	private long timestamp = 0;
	private String owner = null;

	public Event() {}

	public Event(P payload, String label, boolean addOwnerToEvent) {
		this.payload = payload;
		this.label = label;
		timestamp = new Date().getTime();
		if (addOwnerToEvent)
			owner = Config.getContext().getUsername();
	}

	public Event(P payload, String label) {
		this(payload, label, false);
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

	public void setOwner(String owner) {
		if (this.owner == null) {
			this.owner = owner;
		}
	}

	@Override
	public String toString() {
		return "{Label: " + label + ", Timestamp: " + timestamp + ", Owner: " + owner + ", Payload: " + payload + "}";
	}

}
