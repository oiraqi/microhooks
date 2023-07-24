package io.microhooks.brokers.kafka;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import io.microhooks.common.Event;
import io.microhooks.internal.EventProducer;

public class KafkaEventProducer extends EventProducer {

    private KafkaProducer<Long, Event<Object>> producer;

    public KafkaEventProducer(String brokers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
        props.put("value.serializer", "io.microhooks.brokers.kafka.EventSerializer");
        producer = new KafkaProducer<>(props);
    }

    @Override
    protected void doPublish(long id, Event<Object> event, String stream) {
        producer.send(new ProducerRecord<Long, Event<Object>>(stream, id, event));
    }
    
}
