package io.microhooks.internal.util;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;

import io.microhooks.internal.Sinkable;
import jakarta.persistence.EntityManager;

public class SinkHelper {

    public void create(Object sinkEntity, long sourceId, EntityManager em) {
        ((Sinkable) sinkEntity).setMicrohooksSourceId(sourceId);
        em.persist(sinkEntity);
    }

    public void update(Class<?> sinkEntityClass, JsonNode payload, long sourceId, EntityManager em) throws Exception {
        Object sinkEntity = findBySourceId(sinkEntityClass.getName(), sourceId, em);
        if (sinkEntity == null) {
            return;
        }
        
        copyProperties(sinkEntity, payload);
        em.persist(sinkEntity);
    }

    public void delete(Class<?> sinkEntityClass, long sourceId, EntityManager em) {
        Object sinkEntity = findBySourceId(sinkEntityClass.getName(), sourceId, em);
        if (sinkEntity == null) {
            return;
        }
        
        em.remove(sinkEntity);
    }

    private Object findBySourceId(String entityName, long sourceId, EntityManager em) {
        return em.createQuery("SELECT o FROM " + entityName + " o WHERE o.microhooksSourceId = :sourceId")
                .setParameter("sourceId", sourceId).getSingleResult();
    }

    private void copyProperties(Object sinkEntity, JsonNode payload) throws Exception {
        Iterator<Entry<String, JsonNode>> it = payload.fields();
        while (it.hasNext()) {
            Entry<String, JsonNode> field = it.next();
            String fieldName = field.getKey();
            JsonNode fieldValue = field.getValue();
            if (fieldValue.isBoolean()) {
                BeanUtils.setProperty(sinkEntity, fieldName, fieldValue.asBoolean());
            } else if (fieldValue.isTextual()) {
                BeanUtils.setProperty(sinkEntity, fieldName, fieldValue.asText());
            } else if (fieldValue.isInt()) {
                BeanUtils.setProperty(sinkEntity, fieldName, fieldValue.asInt());
            } else if (fieldValue.isLong()) {
                BeanUtils.setProperty(sinkEntity, fieldName, fieldValue.asLong());
            } else if (fieldValue.isDouble()) {
                BeanUtils.setProperty(sinkEntity, fieldName, fieldValue.asDouble());
            }
        }
    }

}
