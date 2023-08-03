package io.microhooks.tests.micronaut.raw;

import io.microhooks.tests.microhooks.raw.boilerplate.SunkEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class SinkEntity extends SunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Override
    public String toString() {
        return "SinkEntity {id: " + id + ", name: " + name + "}";
    }

}
