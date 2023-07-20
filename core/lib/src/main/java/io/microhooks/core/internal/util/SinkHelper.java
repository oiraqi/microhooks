package io.microhooks.core.internal.util;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;

import io.microhooks.core.internal.Sinkable;

public class SinkHelper {


    private ObjectMapper objectMapper = new ObjectMapper();

    public void create(Object sinkEntity, long sourceId, EntityManager em) {
        ((Sinkable) sinkEntity).setMicrohooksSourceId(sourceId);
        em.persist(sinkEntity);
    }

    public void update(Class<?> sinkEntityClass, Object payload, long sourceId, EntityManager em) throws Exception {
        Object sinkEntity = findBySourceId(sinkEntityClass.getName(), sourceId, em);
        if (sinkEntity == null) {
            return;
        }
        BeanUtils.copyProperties(sinkEntity, payload);
        em.persist(sinkEntity);
    }

    private Object findBySourceId(String entityName, long sourceId, EntityManager em) {
        return em.createQuery("SELECT o FROM " + entityName + " o WHERE o.microhooksSourceId = :sourceId")
                .setParameter("sourceId", sourceId).getSingleResult();
    }

}
