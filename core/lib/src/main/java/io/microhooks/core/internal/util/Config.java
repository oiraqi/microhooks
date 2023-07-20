package io.microhooks.core.internal.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import io.microhooks.core.BrokerType;
import io.microhooks.core.ContainerType;
import io.microhooks.core.internal.BrokerNotSupportedException;
import io.microhooks.core.internal.EventProducer;
import io.microhooks.core.internal.EventConsumer;
import io.microhooks.core.internal.Context;

public class Config {

    private static EventProducer eventProducer = null;
    private static EventConsumer eventConsumer = null;
    private static ContainerType containerType = Defaults.CONTAINER_TYPE;
    private static BrokerType brokerType = Defaults.BROKER_TYPE;
    private static String brokerCluster = Defaults.BROKER_CLUSTER;
    private static String serviceName = Defaults.SERVICE_NAME;
    private static boolean addOwnerToEvent = Defaults.ADD_OWNER_TO_EVENT;
    private static Context context = null;

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
                } else if (key.equals("microhooks.container")) {
                    containerType = ContainerType.valueOf(value.toUpperCase());
                } else if (key.equals("microhooks.broker.type")) {
                    brokerType = BrokerType.valueOf(value.toUpperCase());
                } else if (key.equals("microhooks.broker.cluster")) {
                    brokerCluster = value;
                } else if (key.equals("microhooks.events.out.addOwner")) {
                    addOwnerToEvent = Boolean.valueOf(value);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*
         * Iterable<Class<?>> microhooksApp =
         * ClassIndex.getAnnotated(MicrohooksApplication.class);
         * MicrohooksApplication annotation = microhooksApp.iterator().next()
         * .<MicrohooksApplication>getAnnotation(MicrohooksApplication.class);
         */
    }

    public static String getServiceName() {
        return serviceName;
    }

    public static boolean getAddOwnerToEvent() {
        return addOwnerToEvent;
    }

    public static String getBrokerCluster() {
        return brokerCluster;
    }

    public static EventProducer getEventProducer() throws Exception {
        if (eventProducer == null) {
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

    public static Context getContext() {
        if (context == null) {
            try {
                if (containerType == ContainerType.MICRONAUT) {
                    context = (Context) Class.forName("io.microhooks.containers.micronaut.MicronautContext")
                            .getConstructor()
                            .newInstance();
                } else if (containerType == ContainerType.QUARKUS) {
                    return (Context) Class.forName("io.microhooks.containers.quarkus.QuarkusContext").getConstructor()
                            .newInstance();
                } else {
                    context = (Context) Class.forName("io.microhooks.containers.spring.SpringContext").getConstructor()
                        .newInstance();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return context;

    }
}
