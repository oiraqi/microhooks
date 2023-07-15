package io.microhooks.consumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.microhooks.core.ConfigOption;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProcessEvent {
    String stream();
    String label() default "*";
    ConfigOption authenticate() default ConfigOption.APP;
    String authenticationKey() default "";
}
