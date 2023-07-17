package io.microhooks.core;

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
	private String owner = null;
	private String signature = null;

	public Event() {}

	public Event(P payload, String label, boolean addOwnerToEvent) {
		this.payload = payload;
		this.label = label;
		timestamp = new Date().getTime();
		if (addOwnerToEvent)
			owner = "iraqi"; //Config.getSecurityContext().getUsername();
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

	public void setSignature(String signature) {
		if (this.signature == null) {
			this.signature = signature; 
		}
	}

	public void sign(long id) {
		if (this.signature == null) {
			signature = "sig";
		}		
	}

	public boolean verify(long id, String authenticationKey) {
		return true;
	}

	@Override
	public String toString() {
		return "{Label: " + label + ", Timestamp: " + timestamp + ", Owner: " + owner + ", Payload: " + payload + ", Signature: " + signature + "}";
	}

}
