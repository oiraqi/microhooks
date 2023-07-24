package io.microhooks.containers.spring;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.microhooks.internal.Context;

public class SpringContext implements Context {

    @Override
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication instanceof AnonymousAuthenticationToken ? null : authentication.getName();        
    }
    
}
