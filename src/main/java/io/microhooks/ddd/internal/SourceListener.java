package io.microhooks.ddd.internal;

import javax.persistence.PrePersist;

public class SourceListener {
    
    @PrePersist
    public void onPrePersist(Object o) {
        System.out.println("Hi!");
    }
}
