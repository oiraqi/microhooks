package io.microhooks.tests.micronaut.raw.boilerplate;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;

import com.fasterxml.jackson.databind.JsonNode;

import io.microhooks.tests.micronaut.raw.SinkEntity;

@Singleton
public class MicronautSinkRepository implements SinkRepository {

    @PersistenceContext
    protected EntityManager em;

    private SinkHelper sinkHelper = new SinkHelper();

    public void create(SinkEntity sinkEntity, long sourceId) {
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

    public boolean update(JsonNode payload, long sourceId) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            sinkHelper.update(payload, sourceId, em);
            tx.commit();
            return true;
        } catch (Exception ex) {
            tx.rollback();
            return false;
        }
    }

    public void delete(long sourceId) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            sinkHelper.delete(sourceId, em);
            tx.commit();
        } catch (Exception ex) {
        } finally {
            tx.rollback();
        }
    }

}
