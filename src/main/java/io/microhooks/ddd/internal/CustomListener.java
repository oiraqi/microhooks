package io.microhooks.ddd.internal;

import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import io.microhooks.ddd.OnCreate;
import io.microhooks.ddd.OnDelete;
import io.microhooks.ddd.OnUpdate;
import io.microhooks.ddd.TrackedFields;
import io.microhooks.eda.EventProducer;
import io.microhooks.eda.MappedEvent;
import io.microhooks.eda.providers.EventProducerConfig;
import io.microhooks.util.logging.Logged;

public class CustomListener {
    private EventProducer<Object, Object> eventProducer;

    @PostPersist
    @Logged
    @SuppressWarnings("unchecked")
    public void onPostPersist(Object o) throws Exception {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnCreate.class)) {
                List<MappedEvent<Object, Object>> mappedEvents = (List<MappedEvent<Object, Object>>) method.invoke(o);
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
                TrackedFields trackedFields = null;
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
        if (eventProducer == null) {
            eventProducer = new EventProducerConfig().<Object, Object>eventProducer();
        }
        mappedEvents.forEach(mappedEvent -> eventProducer.publish(mappedEvent.getKey(),
                mappedEvent.getPayload(), mappedEvent.getLabel(), mappedEvent.getStreams()));
    }
}
