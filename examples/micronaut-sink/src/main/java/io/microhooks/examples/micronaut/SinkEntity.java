package io.microhooks.examples.micronaut;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import io.microhooks.sink.Sink;
import lombok.Data;

@Entity
@Data
@Sink(stream = "SourceMicroservice-Stream1")
public class SinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Override
    public String toString() {
        return "SinkEntity {id: " + id + ", name: " + name + "}";
    }

}
