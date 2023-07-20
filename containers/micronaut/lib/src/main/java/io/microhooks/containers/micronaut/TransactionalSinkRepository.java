package io.microhooks.containers.micronaut;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;

import io.microhooks.core.internal.SinkRepository;
import io.microhooks.core.internal.util.SinkHelper;

@Singleton
public class TransactionalSinkRepository implements SinkRepository {

    @PersistenceContext
    protected EntityManager em;

    private SinkHelper sinkHelper = new SinkHelper();

    public void create(Object sinkEntity, long sourceId) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        sinkHelper.create(sinkEntity, sourceId, em);
        tx.commit();
    }

    public void update(Class<?> sinkEntityClass, Object payload, long sourceId) throws Exception{
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        sinkHelper.update(sinkEntityClass, payload, sourceId, em);
        tx.commit();
    }

}
