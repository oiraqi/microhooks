package io.microhooks.builder;

import java.util.Map;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.FieldPersistence;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;

public class SourceInstrumenter {

    public static DynamicType.Builder<?> process(DynamicType.Builder<?> builder, Loader loader, boolean isSource, boolean isCustomSource) {
        Class[] listeners = null;
        Class entityListeners = loader.findClass("jakarta.persistence.EntityListeners");
        loader.findClass("io.microhooks.internal.EntityListener");
        Class trackable = loader.findClass("io.microhooks.internal.Trackable");
        Generic map = TypeDescription.Generic.Builder.parameterizedType(Map.class, String.class, String.class)
                .build();
        builder = builder.implement(trackable)
                .defineField("microhooksTrackedFields", map, Visibility.PRIVATE, FieldPersistence.TRANSIENT)
                .defineMethod("getMicrohooksTrackedFields", map, Visibility.PUBLIC)
                .intercept(FieldAccessor.ofField("microhooksTrackedFields"))
                .defineMethod("setMicrohooksTrackedFields", void.class, Visibility.PUBLIC)
                .withParameters(map)
                .intercept(FieldAccessor.ofField("microhooksTrackedFields"));

        if (isCustomSource) {

            Class customSourceListener = loader.findClass("io.microhooks.internal.CustomSourceListener");

            if (isSource) {
                Class sourceListener = loader.findClass("io.microhooks.internal.SourceListener");
                listeners = new Class[2];
                listeners[0] = sourceListener;
                listeners[1] = customSourceListener;
            } else {
                listeners = new Class[1];
                listeners[0] = customSourceListener;
            }
        } else {
            Class sourceListener = loader.findClass("io.microhooks.internal.SourceListener");
            listeners = new Class[1];
            listeners[0] = sourceListener;
        }

        return builder.annotateType(AnnotationDescription.Builder.ofType(entityListeners)
                    .defineTypeArray("value", listeners).build());
    }
}
