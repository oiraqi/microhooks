package io.microhooks.brokers.kafka;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import io.microhooks.core.Event;
import io.microhooks.internal.EventProducer;

public class KafkaEventProducer extends EventProducer {

    private KafkaProducer<String, Event<Object>> producer;

    public KafkaEventProducer(String brokers) {
        Properties props = new Properties();
        System.out.println(brokers);
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all");
        props.put("key.serializer", "io.microhooks.broker.kafka.GenericKafkaSerializer");
        props.put("value.serializer", "io.microhooks.broker.kafka.GenericKafkaSerializer");
        producer = new KafkaProducer<>(props);
    }

    @Override
    protected void publish(String key, Event<Object> event, String stream) {
        producer.send(new ProducerRecord<>(stream, key, event));
    }
    
}
