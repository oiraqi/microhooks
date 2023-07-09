package io.microhooks.tests.spring.kafka;

import org.springframework.data.repository.CrudRepository;

public interface TestRepository extends CrudRepository<TestEntity, Long> {
    
}
