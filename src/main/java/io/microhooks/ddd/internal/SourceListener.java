package io.microhooks.ddd.internal;

import javax.persistence.PostPersist;

public class SourceListener {
    
    @PostPersist
    public void onPostPersist(Object o) {
        System.out.println("Hi!");
    }
}
