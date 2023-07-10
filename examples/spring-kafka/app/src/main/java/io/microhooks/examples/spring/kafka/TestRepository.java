package io.microhooks.examples.spring.kafka;

import org.springframework.data.repository.CrudRepository;

public interface TestRepository extends CrudRepository<TestEntity, Long> {
    
}
