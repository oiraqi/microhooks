package io.microhooks.tests.spring.raw.boilerplate;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaEventProducer {

    private KafkaProducer<Long, Event<Object>> producer;

    public KafkaEventProducer(String brokers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
        props.put("value.serializer", "io.microhooks.examples.spring.raw.boilerplate.EventSerializer");
        producer = new KafkaProducer<>(props);
    }

    public void publish(long id, Event<Object> event, String stream) {
        producer.send(new ProducerRecord<Long, Event<Object>>(stream, id, event));
    }
    
}
