package io.microhooks.producer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.microhooks.core.ConfigOption;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Source {
    String[] mappings();
    ConfigOption sign() default ConfigOption.APP;
    // Tag outgoing events with their respective owners
    ConfigOption addOwnerToEvent() default ConfigOption.APP;
}
