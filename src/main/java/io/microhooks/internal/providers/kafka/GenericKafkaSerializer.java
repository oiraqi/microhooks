package io.microhooks.internal.providers.kafka;

import java.io.IOException;
import org.apache.kafka.common.serialization.Serializer;

import io.microhooks.internal.util.JsonSerdes;

public class GenericKafkaSerializer<T> implements Serializer<T> {

    @Override
    public byte[] serialize(String topic, T data) {
        try {
            return JsonSerdes.getSingleton().serialize(data);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    
}
