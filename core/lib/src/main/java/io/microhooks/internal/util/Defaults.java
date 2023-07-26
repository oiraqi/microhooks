package io.microhooks.internal.util;

import java.util.Random;

public class Defaults {

    public static final String SERVICE_NAME = "Microservice-" + new Random().nextInt();

    public static final String BROKER_CLUSTER = "localhost:9092";

}
