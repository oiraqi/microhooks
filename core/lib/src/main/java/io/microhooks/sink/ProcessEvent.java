package io.microhooks.sink;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProcessEvent {
    String stream();
    String label() default "*";
    boolean prepare() default false; //For Two-Phase Commit
}
