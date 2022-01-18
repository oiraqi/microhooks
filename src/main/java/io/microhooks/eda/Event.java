package io.microhooks.eda;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.Date;

import lombok.Getter;

@Getter
public class Event<U> {
	private final String username;
	private final long timestamp;
	private final U payload;
	private final String label;

	public Event(U payload, String label) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			this.username = authentication.getName();
		} else {
			this.username = null;
		}
		this.timestamp = new Date().getTime();
		this.payload = payload;
		this.label = label;
	}
}
