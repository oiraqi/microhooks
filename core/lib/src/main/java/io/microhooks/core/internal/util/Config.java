package io.microhooks.core.internal.util;

import org.atteo.classindex.ClassIndex;

import io.microhooks.core.BrokerType;
import io.microhooks.core.MicrohooksApplication;
import io.microhooks.core.internal.BrokerNotSupportedException;
import io.microhooks.core.internal.EventProducer;
import io.microhooks.core.internal.NullEventProducer;
import io.microhooks.core.internal.EventConsumer;
import io.microhooks.core.internal.SecurityContext;

public class Config {

    private static EventProducer eventProducer = null;
    private static EventConsumer eventConsumer = null;
    private static BrokerType brokerType = null;
    private static String brokerCluster = null;
    private static String serviceName = null;

    public static String getServiceName() {
        if (serviceName == null) {
            Iterable<Class<?>> microhooksApp = ClassIndex.getAnnotated(MicrohooksApplication.class);
            MicrohooksApplication annotation = microhooksApp.iterator().next().<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
            serviceName = annotation.name();
        }
        return serviceName;
    }

    public static void init() {
        if (brokerType == null || brokerCluster == null) {
            Iterable<Class<?>> microhooksApp = ClassIndex.getAnnotated(MicrohooksApplication.class);
            MicrohooksApplication annotation = microhooksApp.iterator().next().<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
            if (brokerType == null) {
                brokerType = annotation.broker();
            }
            if (brokerCluster == null) {
                brokerCluster = annotation.brokerCluster();
            }
        }        
    }

    public static EventProducer getEventProducer() throws Exception {
        if (eventProducer == null) {            
            init();

            if (brokerType == null) {
                return new NullEventProducer();
            }

            if (brokerCluster == null) {
                brokerCluster = "localhost:9092";
            }

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

            if (brokerType == null) {
                brokerType = BrokerType.KAFKA;
            }

            String brokerCluster = System.getProperty("brokerCluster");
            if (brokerCluster == null) {
                brokerCluster = "localhost:9092";
            }

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
