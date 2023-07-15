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
    // Whether to authenticate/verify incoming events or not
    boolean authenticate() default true;
    // The default public key to authenticate incoming events
    String authenticationKey() default "";
    // Whether to sign outgoing events or not
    // This application's private key to sign outgoing events shall remain secret
    // and shall be fetched from a secure config repo at runtime
    boolean sign() default true;
    // Tag outgoing events with their respective owners
    boolean addOwnerToEvent() default false;
}
