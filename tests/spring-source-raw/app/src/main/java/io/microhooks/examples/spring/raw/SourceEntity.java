package io.microhooks.examples.spring.raw;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import io.microhooks.examples.spring.raw.boilerplate.SourceEntityListener;
import io.microhooks.examples.spring.raw.boilerplate.CustomSourceEntityListener;

import lombok.Data;

@Entity
@Data
@EntityListeners({SourceEntityListener.class, CustomSourceEntityListener.class})
public class SourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    
    private int amount;

}
