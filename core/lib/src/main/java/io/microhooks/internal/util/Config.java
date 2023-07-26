package io.microhooks.internal.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import io.microhooks.internal.EventConsumer;
import io.microhooks.internal.EventProducer;
import io.microhooks.internal.NoSupportedBrokerException;

public class Config {

    private static EventProducer eventProducer = null;
    private static EventConsumer eventConsumer = null;
    private static String brokerCluster = Defaults.BROKER_CLUSTER;
    private static String serviceName = Defaults.SERVICE_NAME;

    public static void init() {

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("src/main/resources/application.properties")))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("microhooks")) {
                    continue;
                }
                StringTokenizer strk = new StringTokenizer(line, "=");
                String key = strk.nextToken().trim();
                String value = strk.nextToken().trim();
                if (key.equals("microhooks.service.name")) {
                    serviceName = value;
                    System.out.println(value);
                } else if (key.equals("microhooks.broker.cluster")) {
                    brokerCluster = value;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        initBrokerType();

    }

    private static void initBrokerType() {
        Class<?> producerClass = null;
        Class<?> consumerClass = null;
        boolean found = false;
        try {
            producerClass = Class.forName("io.microhooks.brokers.kafka.KafkaEventProducer");
            consumerClass = Class.forName("io.microhooks.brokers.kafka.KafkaEventConsumer");
            found = true;
        } catch (Exception e) {
            try {
                producerClass = Class.forName("io.microhooks.brokers.rabbitmq.RabbitMQEventProducer");
                consumerClass = Class.forName("io.microhooks.brokers.rabbitmq.RabbitMQEventConsumer");
                found = true;
            } catch (Exception ex) {
                try {
                    producerClass = Class.forName("io.microhooks.brokers.rocketmq.RocketMQEventProducer");
                    consumerClass = Class.forName("io.microhooks.brokers.rocketmq.RocketMQEventConsumer");
                    Class.forName("io.microhooks.brokers.rocketmq.RocketMQEventConsumer");
                    found = true;
                } catch (Exception exx) {
                }
            }
        }
        try {
            if (found) {
                eventProducer = (EventProducer) producerClass.getDeclaredConstructor(String.class).newInstance(brokerCluster);
                eventConsumer = (EventConsumer) consumerClass.getDeclaredConstructor(String.class).newInstance(brokerCluster);
                return;
            }
        } catch (Exception ex) {
        }

        throw new NoSupportedBrokerException();
    }

    public static String getServiceName() {
        return serviceName;
    }

    public static String getBrokerCluster() {
        return brokerCluster;
    }

    public static EventProducer getEventProducer() throws Exception {
        return eventProducer;
    }

    public static EventConsumer getEventConsumer() throws Exception {
        return eventConsumer;
    }

}
