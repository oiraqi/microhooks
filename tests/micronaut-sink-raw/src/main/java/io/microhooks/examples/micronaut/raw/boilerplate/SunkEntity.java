package io.microhooks.tests.microhooks.raw.boilerplate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class SunkEntity {
    
    @Column(unique=true)
    private long sourceId;
}
