package io.microhooks.examples.spring;

import org.springframework.data.repository.CrudRepository;

public interface SourceRepository extends CrudRepository<SourceEntity, Long> {
    
}
