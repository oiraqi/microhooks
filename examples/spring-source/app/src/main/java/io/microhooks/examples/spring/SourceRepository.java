package io.microhooks.examples.spring;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface SourceRepository extends CrudRepository<SourceEntity, Long> {
    
}
