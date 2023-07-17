package io.microhooks.core.internal.util;

import org.atteo.classindex.ClassIndex;

import io.microhooks.core.BrokerType;
import io.microhooks.core.MicrohooksApplication;
import io.microhooks.core.internal.BrokerNotSupportedException;
import io.microhooks.core.internal.EventProducer;
import io.microhooks.core.internal.EventConsumer;
import io.microhooks.core.internal.SecurityContext;

public class Config {

    private static EventProducer eventProducer = null;
    private static EventConsumer eventConsumer = null;
    private static BrokerType brokerType = null;
    private static String brokerCluster = null;
    private static String serviceName = null;
    private static int authenticate = -1;
    private static String authenticationKey = null;
    private static int sign = -1;
    private static int addOwnerToEvent = -1;

    public static String getServiceName() {
        if (serviceName == null) {
            Iterable<Class<?>> microhooksApp = ClassIndex.getAnnotated(MicrohooksApplication.class);
            MicrohooksApplication annotation = microhooksApp.iterator().next().<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
            serviceName = annotation.name();
        }
        return serviceName;
    }

    public static boolean getAuthenticate() {
        if (authenticate == -1) {
            Iterable<Class<?>> microhooksApp = ClassIndex.getAnnotated(MicrohooksApplication.class);
            MicrohooksApplication annotation = microhooksApp.iterator().next().<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
            authenticate = annotation.authenticate() ? 1:0;
        }
        return authenticate == 1;
    }

    public static String getAuthenticationKey() {
        if (authenticationKey == null) {
            Iterable<Class<?>> microhooksApp = ClassIndex.getAnnotated(MicrohooksApplication.class);
            MicrohooksApplication annotation = microhooksApp.iterator().next().<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
            authenticationKey = annotation.authenticationKey();
        }
        return authenticationKey;
    }

    public static boolean getAddOwnerToEvent() {
        if (addOwnerToEvent == -1) {
            Iterable<Class<?>> microhooksApp = ClassIndex.getAnnotated(MicrohooksApplication.class);
            MicrohooksApplication annotation = microhooksApp.iterator().next().<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
            addOwnerToEvent = annotation.addOwnerToEvent() ? 1:0;
        }
        return addOwnerToEvent == 1;
    }

    public static boolean getSign() {
        if (sign == -1) {
            Iterable<Class<?>> microhooksApp = ClassIndex.getAnnotated(MicrohooksApplication.class);
            MicrohooksApplication annotation = microhooksApp.iterator().next().<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
            sign = annotation.sign() ? 1:0;
        }
        return sign == 1;
    }

    private static void init() {
        if (brokerType == null || brokerCluster == null) {
            Iterable<Class<?>> microhooksApp = ClassIndex.getAnnotated(MicrohooksApplication.class);
            MicrohooksApplication annotation = microhooksApp.iterator().next().<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
            if (brokerType == null) {
                brokerType = annotation.broker();
                if (brokerType == null) {
                    brokerType = BrokerType.KAFKA;
                }
            }
            if (brokerCluster == null) {
                brokerCluster = annotation.brokerCluster();
                if (brokerCluster == null) {
                    brokerCluster = "localhost:9092";
                }
            }
        }        
    }

    public static String getBrokerCluster() {
        if (brokerCluster == null) {
            init();
        }
        return brokerCluster;
    }

    public static EventProducer getEventProducer() throws Exception {
        if (eventProducer == null) {            
            init();

            Class<?> clazz = null;

            if (brokerType == BrokerType.KAFKA) {
                clazz = Class.forName("io.microhooks.brokers.kafka.KafkaEventProducer");
            } else if (brokerType == BrokerType.RABBITMQ) {
                clazz = Class.forName("io.microhooks.brokers.rabbitmq.RabbitMQEventProducer");
            } else if (brokerType == BrokerType.ROCKETMQ) {
                clazz = Class.forName("io.microhooks.brokers.rocketmq.RocketMQEventProducer");
            } else {
                throw new BrokerNotSupportedException(brokerType);
            }

            eventProducer = (EventProducer) clazz.getDeclaredConstructor(String.class).newInstance(brokerCluster);
        }
        return eventProducer;
    }

    public static EventConsumer getEventConsumer() throws Exception {
        if (eventConsumer == null) {
            init();

            Class<?> clazz = null;

            if (brokerType == BrokerType.KAFKA) {
                clazz = Class.forName("io.microhooks.brokers.kafka.KafkaEventConsumer");
            } else if (brokerType == BrokerType.RABBITMQ) {
                clazz = Class.forName("io.microhooks.brokers.rabbitmq.RabbitMQEventConsumer");
            } else if (brokerType == BrokerType.ROCKETMQ) {
                clazz = Class.forName("io.microhooks.brokers.rocketmq.RocketMQEventConsumer");
            } else {
                throw new BrokerNotSupportedException(brokerType);
            }

            eventConsumer = (EventConsumer) clazz.getDeclaredConstructor(String.class).newInstance(brokerCluster);
        }
        return eventConsumer;
    }

    public static SecurityContext getSecurityContext() {
        return null;
    }
}
