package io.microhooks.brokers.kafka;

import java.util.Properties;
import java.time.Duration;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.Consumer;

import io.microhooks.common.Event;
import io.microhooks.internal.EventConsumer;
import io.microhooks.internal.util.CachingReflector;
import io.microhooks.internal.util.Config;

public class KafkaEventConsumer extends EventConsumer {

    private Consumer<Long, Event<JsonNode>> consumer;

    public KafkaEventConsumer(String brokers) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", Config.getBrokerCluster());
        props.setProperty("group.id", Config.getServiceName());
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
        props.setProperty("value.deserializer", "io.microhooks.brokers.kafka.EventDeserializer");

        consumer = new KafkaConsumer<>(props);
    }

    protected void subscribe() {
        consumer.subscribe(CachingReflector.getAllStreams());
        while (true) {
            ConsumerRecords<Long, Event<JsonNode>> records = consumer.poll(Duration.ofSeconds(60));
            records.forEach(record -> {
                processEvent(record.key(), record.value(), record.topic());
            });
        }
    }

}
