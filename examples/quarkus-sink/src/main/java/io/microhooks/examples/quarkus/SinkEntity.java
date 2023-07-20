package io.microhooks.examples.quarkus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import io.microhooks.consumer.Sink;

import lombok.Data;

@Entity
@Data
@Sink(stream = "SourceMicroservice-Stream2")
public class SinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int amount;

}
