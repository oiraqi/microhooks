package io.microhooks.core.internal.util;

import org.atteo.classindex.ClassIndex;

import io.microhooks.consumer.Sink;
import io.microhooks.core.MicrohooksApplication;
import io.microhooks.core.internal.BrokerNotSupportedException;
import io.microhooks.core.internal.EventProducer;
import io.microhooks.core.internal.NullEventProducer;
import io.microhooks.core.internal.EventConsumer;
import io.microhooks.core.internal.SecurityContext;

public class Config {

    private static EventProducer eventProducer = null;
    private static EventConsumer eventConsumer = null;
    private static String brokerType = null;

    public static void initBrokerType() {
        if (brokerType == null) {
            Iterable<Class<?>> microhooksApp = ClassIndex.getAnnotated(MicrohooksApplication.class);
            MicrohooksApplication annotation = microhooksApp.iterator().next().<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
            brokerType = annotation.broker();
        }
    }

    public static EventProducer getEventProducer() throws Exception {
        if (eventProducer == null) {            
            initBrokerType();

            if (brokerType == null) {
                return new NullEventProducer();
            }

            String brokerCluster = System.getProperty("brokerCluster");
            if (brokerCluster == null) {
                brokerCluster = "localhost:9092";
            }

            Class<?> clazz = null;

            if (brokerType.trim().equals("kafka")) {
                clazz = Class.forName("io.microhooks.brokers.KafkaEventProducer");
            } else if (brokerType.trim().equals("rabbitmq")) {
                clazz = Class.forName("io.microhooks.brokers.RabbitMQEventProducer");
            } else if (brokerType.trim().equals("rocketmq")) {
                clazz = Class.forName("io.microhooks.brokers.RocketMQEventProducer");
            } else {
                throw new BrokerNotSupportedException(brokerType);
            }

            eventProducer = (EventProducer) clazz.getDeclaredConstructor(String.class).newInstance(brokerCluster);
        }
        return eventProducer;
    }

    public static EventConsumer getEventConsumer() throws Exception {
        if (eventConsumer == null) {
            initBrokerType();

            if (brokerType == null) {
                brokerType = "kafka";
            }

            String brokerCluster = System.getProperty("brokerCluster");
            if (brokerCluster == null) {
                brokerCluster = "localhost:9092";
            }

            Class<?> clazz = null;

            if (brokerType.trim().equals("kafka")) {
                clazz = Class.forName("io.microhooks.brokers.KafkaEventConsumer");
            } else if (brokerType.trim().equals("rabbitmq")) {
                clazz = Class.forName("io.microhooks.brokers.RabbitMQEventConsumer");
            } else if (brokerType.trim().equals("rocketmq")) {
                clazz = Class.forName("io.microhooks.brokers.RocketMQEventConsumer");
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
