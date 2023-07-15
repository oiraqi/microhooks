package io.microhooks.core;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Event<U> {
	public static final String RECORD_CREATED = "_R_C_";
    public static final String RECORD_UPDATED = "_R_U_";
    public static final String RECORD_DELETED = "_R_D_";

	private final U payload;
	private final String label;
	private final long timestamp;
	private String owner;
	@Getter
	@Setter
	private String signature;

	public Event(U payload, String label, boolean addOwnerToEvent) {
		this.payload = payload;
		this.label = label;
		timestamp = new Date().getTime();
		if (addOwnerToEvent)
			owner = "iraqi"; //Config.getSecurityContext().getUsername();
	}

	public Event(U payload, String label) {
		this(payload, label, false);
	}

	public void sign(long id) {
		signature = "sig";
	}

}
