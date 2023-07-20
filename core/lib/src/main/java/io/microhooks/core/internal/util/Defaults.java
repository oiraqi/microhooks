package io.microhooks.core.internal.util;

import io.microhooks.core.ContainerType;

import java.util.Random;

import io.microhooks.core.BrokerType;

public class Defaults {

    public static final String SERVICE_NAME = "Microservice-" + new Random().nextInt();

    public static final ContainerType CONTAINER_TYPE = ContainerType.SPRING;

    public static final BrokerType BROKER_TYPE = BrokerType.KAFKA;

    public static final String BROKER_CLUSTER = "localhost:9092";

    public static final boolean SIGN = false;

    public static final boolean AUTHENTICATE = false;

    public static final boolean ADD_OWNER_TO_EVENT = false;

}
