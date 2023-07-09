package io.microhooks.internal.util;

import io.microhooks.internal.BrokerNotSupportedException;
import io.microhooks.internal.EventProducer;
import io.microhooks.internal.NullEventProducer;

public class Config {

    public static String getAppName() {
        return System.getProperty("appName");
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
            clazz = Class.forName("io.microhooks.internal.providers.KafkaEventProducer");
        } else if (brokerType.trim().equals("rabbitmq")) {
            clazz = Class.forName("io.microhooks.internal.providers.RabbitMQEventProducer");
        } else {
            throw new BrokerNotSupportedException(brokerType);
        }
        
        return (EventProducer)clazz.getDeclaredConstructor(String.class).newInstance(brokerCluster);
        
        
    }
}
