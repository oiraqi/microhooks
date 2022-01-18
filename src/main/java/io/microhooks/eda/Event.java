package io.microhooks.eda;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.Date;

import lombok.Getter;

@Getter
public class Event<T, U> {
	private final String username;
	private final long timestamp;
	private final T key;
	private U payload;
	private String label;

	public Event(T key, U payload, String label) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			this.username = authentication.getName();
		} else {
			this.username = null;
		}
		this.timestamp = new Date().getTime();
		this.key = key;
		this.payload = payload;
		this.label = label;
	}
}
