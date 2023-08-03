package io.microhooks.tests.spring.raw;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Arrays;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import io.microhooks.tests.spring.raw.boilerplate.SourceEntityListener;
import io.microhooks.tests.spring.raw.boilerplate.CustomSourceEntityListener;
import io.microhooks.tests.spring.raw.boilerplate.TrackedEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@EntityListeners({SourceEntityListener.class, CustomSourceEntityListener.class})
public class SourceEntity extends TrackedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    
    private int amount;

}
