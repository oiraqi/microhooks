package io.microhooks.brokers.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Arrays;
import java.util.Properties;

import io.microhooks.core.internal.EventConsumer;

public class KafkaEventConsumer extends EventConsumer {

    public KafkaEventConsumer(String brokers) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "KafkaEventConsumer");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, EventDeserializer.class);
    }
    protected void subscribeWithBroker() {

    }
    //processEvent(long sourceId, Event<Object> event, String stream)
}
