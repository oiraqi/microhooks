package io.microhooks.eda.providers.kafka;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import io.microhooks.eda.Event;
import io.microhooks.eda.EventProducer;

public class KafkaEventProducer<T, U> extends EventProducer<T, U> {

    private KafkaProducer<T, Event<T, U>> producer;
    private static final String BOOTSTRAP_SERVERS = "${io.microhooks.providers.broker.cluster}";

    public KafkaEventProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("acks", "all");
        props.put("key.serializer", "io.microhooks.providers.kafka.GenericKafkaSerializer");
        props.put("value.serializer", "io.microhooks.providers.kafka.GenericKafkaSerializer");
        producer = new KafkaProducer<>(props);
    }

    @Override
    public void publish(T key, Event<T, U> event, String stream) {
        producer.send(new ProducerRecord<>("${appName}#" + stream, key, event));
    }
    
}
