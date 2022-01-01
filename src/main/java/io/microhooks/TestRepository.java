package io.microhooks;

import org.springframework.data.repository.CrudRepository;

public interface TestRepository extends CrudRepository<TestEntity, Long> {
    
}
