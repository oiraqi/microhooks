package io.microhooks.core.internal.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import io.microhooks.core.internal.NoSupportedBrokerException;
import io.microhooks.core.internal.EventProducer;
import io.microhooks.core.internal.EventConsumer;
import io.microhooks.core.internal.Context;

public class Config {

    private static EventProducer eventProducer = null;
    private static EventConsumer eventConsumer = null;
    private static String brokerCluster = Defaults.BROKER_CLUSTER;
    private static String serviceName = Defaults.SERVICE_NAME;
    private static boolean addOwnerToEvent = Defaults.ADD_OWNER_TO_EVENT;
    private static Context context = null;

    public static void init() {
        initContext();
        initBrokerType();

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
                } else if (key.equals("microhooks.broker.cluster")) {
                    brokerCluster = value;
                } else if (key.equals("microhooks.events.out.addOwner")) {
                    addOwnerToEvent = Boolean.valueOf(value);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void initContext() {
        try {
            Class.forName("io.microhooks.containers.spring.SpringApplicationBootstrap");
            context = (Context) Class.forName("io.microhooks.containers.spring.SpringContext").getConstructor()
                            .newInstance();
        } catch (Exception e) {
            try {
                Class.forName("io.microhooks.containers.micronaut.MicronautApplicationBootstrap");
                context = (Context) Class.forName("io.microhooks.containers.micronaut.MicronautContext")
                            .getConstructor()
                            .newInstance();
            } catch (Exception ex) {
                try {
                    Class.forName("io.microhooks.containers.quarkus.QuarkusApplicationBootstrap");
                    context = (Context) Class.forName("io.microhooks.containers.quarkus.QuarkusContext").getConstructor()
                            .newInstance();
                } catch (Exception exx) {
                }
            }
        }
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

    public static boolean getAddOwnerToEvent() {
        return addOwnerToEvent;
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

    public static Context getContext() {
        return context;

    }
}
