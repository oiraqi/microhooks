package io.microhooks.containers.spring;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.microhooks.core.internal.util.ClassScanner;

import org.springframework.transaction.annotation.Transactional;

@Component
public class EventSubscriptionSetup extends io.microhooks.core.internal.EventSubscriptionSetup {

    @PersistenceContext
    EntityManager em;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        getEventConsumer().subscribe(em, getSinkMap(), getCustomSinkMap());
    }

}