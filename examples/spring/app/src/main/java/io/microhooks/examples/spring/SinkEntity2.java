package io.microhooks.examples.spring;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.microhooks.consumer.Sink;
import io.microhooks.core.Event;
import io.microhooks.producer.OnCreate;
import io.microhooks.producer.Track;
import lombok.Data;

@Entity
@Data
@Sink(stream = "Stream2")
public class SinkEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Track
    private String name;

    @OnCreate(stream = "CustomStream")
    public Event<String> onCreate() {
        return new Event<>(name, "CustomCreate");
    }

}
