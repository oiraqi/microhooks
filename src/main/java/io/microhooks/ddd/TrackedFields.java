package io.microhooks.ddd;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

public class TrackedFields {

    private HashMap<String, CurrentAndPreviousValues> fields = new HashMap<>();

    public void put(String fieldName, Object current) {
        CurrentAndPreviousValues currentAndPreviousValues = fields.get(fieldName);
        if(currentAndPreviousValues != null) {
            currentAndPreviousValues.setPrevious(currentAndPreviousValues.getCurrent());
            currentAndPreviousValues.setCurrent(current);
        } else {
            currentAndPreviousValues = new CurrentAndPreviousValues(current, current);
            fields.put(fieldName, currentAndPreviousValues);
        }
    }

    public Object getCurrent(String fieldName) {
        CurrentAndPreviousValues currentAndPreviousValues = fields.get(fieldName);
        if(currentAndPreviousValues == null) {
            throw new UntrackedFieldException(fieldName);
        }
        return currentAndPreviousValues.getCurrent();
    }

    public Object getPrevious(String fieldName) {
        CurrentAndPreviousValues currentAndPreviousValues = fields.get(fieldName);
        if(currentAndPreviousValues == null) {
            throw new UntrackedFieldException(fieldName);
        }
        return currentAndPreviousValues.getPrevious();
    }

    public boolean didChange(String fieldName) {
        CurrentAndPreviousValues currentAndPreviousValues = fields.get(fieldName);
        if(currentAndPreviousValues == null) {
            throw new UntrackedFieldException(fieldName);
        }
        return currentAndPreviousValues.getClass() != currentAndPreviousValues.getPrevious();
    }
    
    @Data
    @AllArgsConstructor
    private class CurrentAndPreviousValues {
        private Object current;
        private Object previous;        
    }
}
