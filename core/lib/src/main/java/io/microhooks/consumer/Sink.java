package io.microhooks.consumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.atteo.classindex.IndexAnnotated;

import io.microhooks.core.ConfigOption;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@IndexAnnotated
public @interface Sink {
    String stream();
    ConfigOption authenticate() default ConfigOption.APP;
    String authenticationKey() default "";
}
