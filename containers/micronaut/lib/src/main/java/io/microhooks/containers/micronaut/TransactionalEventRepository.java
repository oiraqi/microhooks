package io.microhooks.containers.micronaut;

import io.microhooks.core.internal.EventRepository;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;

@Singleton
public class TransactionalEventRepository implements EventRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Object entity) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(entity);
        tx.commit();
    }
}
