package io.microhooks.internal;

import java.lang.reflect.Field;

import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import io.microhooks.internal.util.Reflector;
import io.microhooks.internal.util.logging.Logged;
import io.microhooks.producer.Source;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SourceListener extends Listener {

    private static final String CREATED = "C";
    private static final String UPDATED = "U";
    private static final String DELETED = "D";

    private ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, Object> mappings = null;
    EventProducer eventProducer = getEventProducer();

    @PostPersist
    @Logged
    public void onPostPersist(Object entity) throws Exception {
        publish(entity, CREATED);
        System.out.println(entity);
    }

    @PostUpdate
    @Logged
    public void onPostUpdate(Object entity) throws Exception {
        publish(entity, UPDATED);
    }

    @PostRemove
    @Logged
    public void onPostRemove(Object entity) throws Exception {
        publish(entity, DELETED);
    }

    private void publish(Object entity, String operation) throws Exception {
        String key = getId(entity).toString();
        Iterator<Entry<String, Object>> iterator = getMappings(entity).entrySet().iterator();        
        while(iterator.hasNext()) {
            Entry<String, Object> mapping = iterator.next();
            String stream = mapping.getKey();
            Object dto = mapping.getValue();
            eventProducer.publish(key, dto, operation, stream);
        }
    }

    private Object getId(Object entity) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                return Reflector.getFieldValue(entity, field.getName());
            }
        }
        throw new IdNotFoundException();
    }

    private Map<String, Object> getMappings(Object entity) throws Exception {
        if (mappings == null) {
            mappings = new HashMap<>();
            Source source = entity.getClass().<Source>getAnnotation(Source.class);

            try {
                for (String mapping : source.mappings()) {
                    StringTokenizer strTok = new StringTokenizer(mapping, ":");
                    String stream = strTok.nextToken();
                    String dto = strTok.nextToken();
                    Class<?> dtoClass = Class.forName(dto);
                    mappings.put(stream, objectMapper.convertValue(entity, dtoClass));
                }

            } catch (Exception ex) {

            }
        }
        return mappings;
    }
}
