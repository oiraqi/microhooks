package io.microhooks.core;

import java.util.Date;

import lombok.Getter;

@Getter
public class Event<U> {
	public static final String RECORD_CREATED = "_R_C_";
    public static final String RECORD_UPDATED = "_R_U_";
    public static final String RECORD_DELETED = "_R_D_";

	@Getter
	private final U payload;
	private final String label;
	private final long timestamp;
	private final String owner;

	public Event(U payload, String label) {		
		this.payload = payload;
		this.label = label;
		timestamp = new Date().getTime();
		owner = "iraqi"; //Config.getSecurityContext().getUsername();
	}

}
