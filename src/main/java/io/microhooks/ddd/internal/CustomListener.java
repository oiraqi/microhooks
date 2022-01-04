package io.microhooks.ddd.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.springframework.beans.factory.annotation.Autowired;

import io.microhooks.ddd.OnCreate;
import io.microhooks.ddd.OnDelete;
import io.microhooks.ddd.OnUpdate;
import io.microhooks.ddd.Track;
import io.microhooks.eda.EventProducer;
import io.microhooks.eda.MappedEvent;
import io.microhooks.util.logging.Logged;

public class CustomListener {
    @Autowired
    private EventProducer<Object, Object> eventProducer;
    private TrackedFields trackedFields = new TrackedFields();

    @PostPersist
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostPersist(Object entity) throws Exception {
        Field[] fields = entity.getClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Track.class)) {
                trackedFields.put(entity.getClass().getName() + ".1", field.getName(), field.get(entity));
            }
        }
        for (Method method : entity.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnCreate.class)) {
                List<MappedEvent<Object, Object>> mappedEvents = (List<MappedEvent<Object, Object>>) method.invoke(entity);
                publish(mappedEvents);
                return;
            }
        }
    }

    @PostUpdate
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostUpdate(Object o) throws Exception {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnUpdate.class)) {
                List<MappedEvent<Object, Object>> mappedEvents = (List<MappedEvent<Object, Object>>) method.invoke(o,
                        trackedFields);
                publish(mappedEvents);
                return;
            }
        }
    }

    @PostRemove
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostRemove(Object o) throws Exception {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnDelete.class)) {
                List<MappedEvent<Object, Object>> mappedEvents = (List<MappedEvent<Object, Object>>) method.invoke(o);
                publish(mappedEvents);
                return;
            }
        }
    }

    private void publish(List<MappedEvent<Object, Object>> mappedEvents) {
        mappedEvents.forEach(mappedEvent -> eventProducer.publish(mappedEvent.getKey(),
                mappedEvent.getPayload(), mappedEvent.getLabel(), mappedEvent.getStreams()));
    }
}
