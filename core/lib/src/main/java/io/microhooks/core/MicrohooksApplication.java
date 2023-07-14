package io.microhooks.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.atteo.classindex.IndexAnnotated;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@IndexAnnotated
public @interface MicrohooksApplication {
    String container() default "spring";
    String broker() default "kafka";
    String brokerCluster() default "localhost:9092";
}
