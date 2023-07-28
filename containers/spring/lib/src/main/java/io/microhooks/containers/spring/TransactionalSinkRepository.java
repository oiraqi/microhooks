package io.microhooks.containers.spring;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import io.microhooks.internal.SinkRepository;
import io.microhooks.internal.util.SinkHelper;

@Component
@Transactional
public class TransactionalSinkRepository implements SinkRepository {
    
    @PersistenceContext
    EntityManager em;

    private SinkHelper sinkHelper = new SinkHelper();

    public void create(Object sinkEntity, long sourceId) {
        sinkHelper.create(sinkEntity, sourceId, em);
    }

    public boolean update(Class<?> sinkEntityClass, JsonNode payload, long sourceId) {
        try {
            sinkHelper.update(sinkEntityClass, payload, sourceId, em);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void delete(Class<?> sinkEntityClass, long sourceId) {
        sinkHelper.delete(sinkEntityClass, sourceId, em);
    }

}
