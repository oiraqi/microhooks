package io.microhooks.containers.spring;

import org.springframework.stereotype.Component;

import io.microhooks.core.internal.EventRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class TransactionalEventRepository implements EventRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Object entity) {
        em.persist(entity);
    }
}
