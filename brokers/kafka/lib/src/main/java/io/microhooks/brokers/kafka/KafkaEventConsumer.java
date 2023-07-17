package io.microhooks.brokers.kafka;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Pattern;
import java.time.Duration;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.Consumer;

import io.microhooks.core.Event;
import io.microhooks.core.internal.EventConsumer;
import io.microhooks.core.internal.util.CachingReflector;
import io.microhooks.core.internal.util.Config;

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
        consumer.subscribe(CachingReflector.getSinkMap().keySet());
        while (true) {
            ConsumerRecords<Long, Event<JsonNode>> identifiedEvents = consumer.poll(Duration.ofSeconds(60));
            identifiedEvents.forEach(record -> {
                processEvent(record.key(), record.value(), record.topic());
            });
        }
    }

}
