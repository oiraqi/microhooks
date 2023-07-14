package io.microhooks.consumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.atteo.classindex.IndexAnnotated;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SourceId {
    
}
