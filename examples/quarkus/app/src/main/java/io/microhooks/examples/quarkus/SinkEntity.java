package io.microhooks.examples.spring;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.microhooks.consumer.Sink;
import io.microhooks.core.Event;

import lombok.Data;

@Entity
@Data
@Sink(stream = "Stream2")
public class SinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int amount;

}
