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
    String name();
    ContainerType container() default ContainerType.SPRING;
    BrokerType broker() default BrokerType.KAFKA;
    String brokerCluster() default "localhost:9092";
    boolean authenticate() default true; // Whether to authenticate/verify incoming events or not
    String authenticationKey() default ""; // The default public key to authenticate incoming events
    boolean sign() default true; // Whether to sign outgoing events or not
    String signingKey() default ""; // This application's public key to sign outgoing events
    boolean addOwnerToEvent() default false; // Tag outgoing events with their respective owners
}
