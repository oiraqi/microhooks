package io.microhooks.containers.micronaut;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;

import com.fasterxml.jackson.databind.JsonNode;

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
        try {
            sinkHelper.create(sinkEntity, sourceId, em);
            tx.commit();
        } catch (Exception ex) {
        } finally {
            tx.rollback();
        }
    }

    public void update(Class<?> sinkEntityClass, JsonNode payload, long sourceId) throws Exception{
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            sinkHelper.update(sinkEntityClass, payload, sourceId, em);
            tx.commit();
        } catch (Exception ex) {
        } finally {
            tx.rollback();
        }
    }

}
