package io.microhooks.internal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.microhooks.common.Event;

public abstract class EventConsumer {

    private SinkRepository sinkRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    public void launch(SinkRepository sinkRepository) {
        this.sinkRepository = sinkRepository;
        subscribe();
    }

    public void processEvent(long sourceId, Event<JsonNode> event, String stream) {
        String label = event.getLabel();
        List<Class<?>> sinkEntityClassList = Context.getSinks(stream);

        if (label != null) {            
            if (label.equals(Event.RECORD_CREATED)) {
                handleRecordCreatedEvent(sourceId, event, sinkEntityClassList);
                return;
            }

            if (label.equals(Event.RECORD_UPDATED)) {
                handleRecordUpdatedEvent(sourceId, event, sinkEntityClassList);
                return;
            }

            if (label.equals(Event.RECORD_DELETED)) {
                handleRecordDeletedEvent(sourceId, sinkEntityClassList);
                return;
            } else {
                handleCustomEvent(sourceId, event, stream);
            }
        } else {
            handleCustomEvent(sourceId, event, stream);
        }

    }

    private void handleRecordCreatedEvent(long sourceId, Event<JsonNode> event, List<Class<?>> sinkEntityClassList) {
        for (Class<?> sinkEntityClass : sinkEntityClassList) {
            try {
                Object sinkEntity = objectMapper.convertValue(event.getPayload(), sinkEntityClass);
                sinkRepository.create(sinkEntity, sourceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRecordUpdatedEvent(long sourceId, Event<JsonNode> event, List<Class<?>> sinkEntityClassList) {        
        Iterator<Class<?>> sinkEntityClassIterator = sinkEntityClassList.iterator();
        while (sinkEntityClassIterator.hasNext()) {
            Class<?> sinkEntityClass = sinkEntityClassIterator.next();
            try {
                if (!sinkRepository.update(sinkEntityClass, event.getPayload(), sourceId)) {
                    // We missed the creation of this record at the source because this stream
                    // has been added later. Let's catch up and create it
                    handleRecordCreatedEvent(sourceId, event, Arrays.asList(sinkEntityClass));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRecordDeletedEvent(long sourceId, List<Class<?>> sinkEntityClassList) {        
        Iterator<Class<?>> sinkEntityClassIterator = sinkEntityClassList.iterator();
        while (sinkEntityClassIterator.hasNext()) {
            Class<?> sinkEntityClass = sinkEntityClassIterator.next();
            try {
                sinkRepository.delete(sinkEntityClass, sourceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCustomEvent(long sourceId, Event<JsonNode> event, String stream) {
        List<Object> customSinks = Context.getCustomSinks(stream);
        if (customSinks == null) {
            return;
        }
        for (Object customSink : customSinks) {
            Map<Method, String> map = Context.getProcessEventMethodsToInvoke(stream, customSink);
            map.entrySet().forEach(entry -> {
                Method method = entry.getKey();
                String label = entry.getValue();
                if (label.equals("*") || label.equals(event.getLabel())) {
                    try {
                        method.invoke(customSink, sourceId, event);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    protected abstract void subscribe();

}
