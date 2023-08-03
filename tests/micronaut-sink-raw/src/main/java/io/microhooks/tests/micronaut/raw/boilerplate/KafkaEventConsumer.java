package io.microhooks.tests.micronaut.raw.boilerplate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;

import java.io.ObjectInputFilter.Config;
import java.lang.reflect.Method;
import java.time.Duration;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.tests.micronaut.raw.SinkEntity;
import io.microhooks.tests.micronaut.raw.SinkService;

public class KafkaEventConsumer {

    private Consumer<Long, Event<JsonNode>> consumer;
    private SinkRepository sinkRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    public KafkaEventConsumer(String brokers) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "SinkMicroservice1");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
        props.setProperty("value.deserializer", "io.microhooks.tests.micronaut.raw.boilerplate.EventDeserializer");

        consumer = new KafkaConsumer<>(props);
    }

    public void launch(SinkRepository sinkRepository) {
        this.sinkRepository = sinkRepository;
        subscribe();
    }

    public void processEvent(long sourceId, Event<JsonNode> event, String stream) {
        String label = event.getLabel();

        if (stream != null && stream.equals("SourceMicroservice-Stream1")) {
            if (label.equals(Event.RECORD_CREATED)) {
                handleRecordCreatedEvent(sourceId, event);
                return;
            }

            if (label.equals(Event.RECORD_UPDATED)) {
                handleRecordUpdatedEvent(sourceId, event);
                return;
            }

            if (label.equals(Event.RECORD_DELETED)) {
                handleRecordDeletedEvent(sourceId);
                return;
            }
        } else if (stream != null && (stream.equals("SourceMicroservice-CustomStream") ||
            stream.equals("SourceMicroservice-CustomStream1"))){
            handleCustomEvent(sourceId, event);
        }

    }

    private void handleRecordCreatedEvent(long sourceId, Event<JsonNode> event) {        
        try {
            SinkEntity sinkEntity = objectMapper.<SinkEntity>convertValue(event.getPayload(), SinkEntity.class);
            sinkRepository.create(sinkEntity, sourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRecordUpdatedEvent(long sourceId, Event<JsonNode> event) {
        try {
            if (!sinkRepository.update(event.getPayload(), sourceId)) {
                // We missed the creation of this record at the source because this stream
                // has been added later. Let's catch up and create it
                handleRecordCreatedEvent(sourceId, event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRecordDeletedEvent(long sourceId) {
        try {
            sinkRepository.delete(sourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCustomEvent(long sourceId, Event<JsonNode> event) {
        Set<SinkService> customSinks = CustomSinks.get();
        if (customSinks == null) {
            return;
        }
        for (SinkService customSink : customSinks) {
            if (event.getLabel().equals("NameChanged")) {
                customSink.processNameChangedEvent(sourceId, event);
            } else if (event.getLabel().equals("Greetings")) {
                customSink.processGreetingsEvents(sourceId, event);
            }
        }
    }

    private void subscribe() {
        consumer.subscribe(Arrays.asList("SourceMicroservice1-Stream1", "SourceMicroservice1-CustomStream", "SourceMicroservice1-CustomStream1"));
        while (true) {
            ConsumerRecords<Long, Event<JsonNode>> records = consumer.poll(Duration.ofSeconds(60));
            records.forEach(record -> {
                processEvent(record.key(), record.value(), record.topic());
            });
        }
    }

}
