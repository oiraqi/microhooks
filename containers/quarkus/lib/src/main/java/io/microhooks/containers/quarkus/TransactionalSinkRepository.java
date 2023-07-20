package io.microhooks.containers.quarkus;


import jakarta.enterprise.context.ApplicationScoped;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import io.microhooks.core.internal.SinkRepository;
import io.microhooks.core.internal.util.SinkHelper;

@ApplicationScoped
@Transactional
public class TransactionalSinkRepository implements SinkRepository {
    
    @PersistenceContext
    EntityManager em;

    private SinkHelper sinkHelper = new SinkHelper();

    public void create(Object sinkEntity, long sourceId) {
        sinkHelper.create(sinkEntity, sourceId, em);
    }

    public void update(Class<?> sinkEntityClass, Object payload, long sourceId) throws Exception{
        sinkHelper.update(sinkEntityClass, payload, sourceId, em);
    }

}
