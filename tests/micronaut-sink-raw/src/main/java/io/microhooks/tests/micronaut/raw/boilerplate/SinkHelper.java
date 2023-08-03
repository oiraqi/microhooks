package io.microhooks.tests.micronaut.raw.boilerplate;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.EntityManager;

import io.microhooks.tests.micronaut.raw.SinkEntity;

public class SinkHelper {

    public void create(SinkEntity sinkEntity, long sourceId, EntityManager em) {
        sinkEntity.setSourceId(sourceId);
        em.persist(sinkEntity);
    }

    public boolean update(JsonNode payload, long sourceId, EntityManager em) throws Exception {
        SinkEntity sinkEntity = findBySourceId(sourceId, em);
        if (sinkEntity == null) {
            return false;
        }
        
        sinkEntity.setName(payload.at("/name").asText());
        em.persist(sinkEntity);
        return true;
    }

    public void delete(long sourceId, EntityManager em) {
        Object sinkEntity = findBySourceId(sourceId, em);
        if (sinkEntity == null) {
            return;
        }
        
        em.remove(sinkEntity);
    }

    private SinkEntity findBySourceId(long sourceId, EntityManager em) {
        return em.<SinkEntity>createQuery("SELECT o FROM io.microhooks.tests.micronaut.raw.SinkEntity o WHERE o.sourceId = :sourceId", SinkEntity.class)
                .setParameter("sourceId", sourceId).getSingleResult();
    }

}
