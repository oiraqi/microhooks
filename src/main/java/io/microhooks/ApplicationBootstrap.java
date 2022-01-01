package io.microhooks;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

import io.microhooks.ddd.internal.SourceListener;

import javax.persistence.EntityListeners;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;

public class ApplicationBootstrap implements ApplicationListener<ApplicationPreparedEvent> {

    public void onApplicationEvent(ApplicationPreparedEvent ev) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
        ByteBuddyAgent.install();
        // lookup entity classes with @source
        new ByteBuddy()
                .redefine(TestEntity.class)
                .annotateType(AnnotationDescription.Builder.ofType(EntityListeners.class)
                        .defineTypeArray("value", SourceListener.class)
                        .build())
                .make()
                .load(TestEntity.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent());
    }
}
