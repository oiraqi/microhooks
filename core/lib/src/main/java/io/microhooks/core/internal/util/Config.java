package io.microhooks.core.internal.util;

import io.microhooks.core.internal.BrokerNotSupportedException;
import io.microhooks.core.internal.EventProducer;
import io.microhooks.core.internal.NullEventProducer;
import io.microhooks.core.internal.EventConsumer;
import io.microhooks.core.internal.SecurityContext;

public class Config {

    public static String getServiceName() {
        return System.getProperty("serviceName");
    }

    public static EventProducer getEventProducer() throws Exception {
        String brokerType = System.getProperty("brokerType");

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
        
        return (EventProducer)clazz.getDeclaredConstructor(String.class).newInstance(brokerCluster);
                
    }

    public static EventConsumer getEventConsumer() throws Exception {
        String brokerType = System.getProperty("brokerType");

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
        
        return (EventConsumer)clazz.getDeclaredConstructor(String.class).newInstance(brokerCluster);
                
    }

    public static SecurityContext getSecurityContext() {
        return null;
    }
}
