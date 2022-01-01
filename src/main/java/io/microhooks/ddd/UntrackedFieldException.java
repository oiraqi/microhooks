package io.microhooks.ddd;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UntrackedFieldException extends RuntimeException {
    
    private final String fieldName;

    @Override
    public String getMessage() {
        return "Untracked field: " + fieldName + ". Make sure to mark it with @Track.";
    }
}
