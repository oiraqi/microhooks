package io.microhooks.brokers.kafka;

import java.util.Properties;
import java.util.Set;
import java.time.Duration;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.Consumer;

import io.microhooks.common.Event;
import io.microhooks.internal.EventConsumer;
import io.microhooks.internal.Context;
import io.microhooks.internal.util.Config;
import io.microhooks.internal.util.Monitor;

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

    protected void subscribe(Set<String> streams) {
        consumer.subscribe(streams);
        while (true) {
            ConsumerRecords<Long, Event<JsonNode>> records = consumer.poll(Duration.ofSeconds(60));
            records.forEach(record -> {
                long startTime = System.nanoTime();

                processEvent(record.key(), record.value(), record.topic());
                
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                String label = record.value().getLabel();
                if (label.equals(Event.RECORD_CREATED)) {
                    Monitor.sourceCount++;
                    Monitor.sourceTotalTime += duration;
                    if (Monitor.sourceCount % 1000 == 0) {
                        System.out.println("Create count: " + Monitor.sourceCount);
                        System.out.println("Create avg: " + (float)Monitor.sourceTotalTime / Monitor.sourceCount);
                    }
                } else if (!label.equals(Event.RECORD_UPDATED) && !label.equals(Event.RECORD_DELETED)) {
                    Monitor.customCount++;
                    Monitor.customTotalTime += duration;
                    if (Monitor.customCount % 1000 == 0) {
                        System.out.println("Custom count: " + Monitor.customCount);
                        System.out.println("Custom avg: " + (float)Monitor.customTotalTime / Monitor.customCount);
                    }
                }
            });
        }
    }

}
