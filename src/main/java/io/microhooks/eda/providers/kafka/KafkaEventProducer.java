package io.microhooks.eda.providers.kafka;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import io.microhooks.eda.Event;
import io.microhooks.eda.EventProducer;
import io.microhooks.eda.LabeledPayload;

public class KafkaEventProducer<T, U> extends EventProducer<T, U> {

    private KafkaProducer<T, LabeledPayload<U>> producer;

    public KafkaEventProducer(String brokers) {
        Properties props = new Properties();
        System.out.println(brokers);
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all");
        props.put("key.serializer", "io.microhooks.eda.providers.kafka.GenericKafkaSerializer");
        props.put("value.serializer", "io.microhooks.eda.providers.kafka.GenericKafkaSerializer");
        producer = new KafkaProducer<>(props);
    }

    @Override
    public void publish(Event<T, U> event, String stream) {
        producer.send(new ProducerRecord<>(stream, event.getKey(), event.getLabeledPayload()));
    }
    
}
