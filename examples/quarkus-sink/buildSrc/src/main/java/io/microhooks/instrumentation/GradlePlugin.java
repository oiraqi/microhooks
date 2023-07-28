package io.microhooks.instrumentation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.modifier.FieldPersistence;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.asm.Advice;

public class GradlePlugin implements net.bytebuddy.build.Plugin {

    private static class Loader extends ClassLoader {
        private ClassFileLocator classFileLocator;

        public Loader(ClassFileLocator classFileLocator) {
            this.classFileLocator = classFileLocator;
        }

        public Class<?> findClass(String name) {
            byte[] bytes;
            try {
                bytes = classFileLocator.locate(name).resolve();
                return defineClass(name, bytes, 0, bytes.length);
            } catch (Exception e) {
                if (!name.equals("io.microhooks.containers.spring.Config"))
                    e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean matches(TypeDescription target) {
        String annotations = target.getDeclaredAnnotations().toString();
        return  annotations.contains("@io.microhooks.source.Source") ||
                annotations.contains("@io.microhooks.source.CustomSource") ||
                annotations.contains("@io.microhooks.sink.Sink") ||
                annotations.contains("@io.microhooks.sink.CustomSink") ||
                annotations.contains("@io.microhooks.common.Dto") ||
                annotations.contains("@org.springframework.boot.autoconfigure.SpringBootApplication");
    }

    @Override
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription target,
            ClassFileLocator classFileLocator) {

        String annotations = target.getDeclaredAnnotations().toString();
        boolean isSource = annotations.contains("@io.microhooks.source.Source");
        boolean isCustomSource = annotations.contains("@io.microhooks.source.CustomSource");

        Loader loader = new Loader(classFileLocator);

        if (isSource || isCustomSource) {
            Class[] listeners = null;
            Class entityListeners = loader.findClass("jakarta.persistence.EntityListeners");
            loader.findClass("io.microhooks.internal.Listener");

            if (isCustomSource) {
                Class trackable = loader.findClass("io.microhooks.internal.Trackable");
                Generic map = TypeDescription.Generic.Builder.parameterizedType(Map.class, String.class, Object.class)
                        .build();
                builder = builder.implement(trackable)
                        .defineField("microhooksTrackedFields", map, Visibility.PRIVATE, FieldPersistence.TRANSIENT)
                        .defineMethod("getMicrohooksTrackedFields", map, Visibility.PUBLIC)
                        .intercept(FieldAccessor.ofField("microhooksTrackedFields"))
                        .defineMethod("setMicrohooksTrackedFields", void.class, Visibility.PUBLIC)
                        .withParameters(map)
                        .intercept(FieldAccessor.ofField("microhooksTrackedFields"));
                
                Class customListener = loader.findClass("io.microhooks.internal.CustomListener");
                
                if (isSource) {
                    Class sourceListener = loader.findClass("io.microhooks.internal.SourceListener");
                    listeners = new Class[2];
                    listeners[0] = sourceListener;
                    listeners[1] = customListener;
                } else {
                    listeners = new Class[1];
                    listeners[0] = customListener;
                }
            } else {
                Class sourceListener = loader.findClass("io.microhooks.internal.SourceListener");
                listeners = new Class[1];
                listeners[0] = sourceListener;
            }
            return builder.annotateType(AnnotationDescription.Builder.ofType(entityListeners)
                            .defineTypeArray("value", listeners).build());
        } 
        boolean isSink = annotations.contains("@io.microhooks.sink.Sink");
        boolean isCustomSink = annotations.contains("@io.microhooks.sink.CustomSink");
        if (isSink || isCustomSink) {
            if(isSink) {
                Class sinkable = loader.findClass("io.microhooks.internal.Sinkable");
                Class unique = loader.findClass("jakarta.persistence.Column");
                builder = builder.implement(sinkable)
                    .defineField("microhooksSourceId", long.class, Visibility.PRIVATE)
                    .annotateField(AnnotationDescription.Builder.ofType(unique).define("unique", true).build())
                    .defineMethod("getMicrohooksSourceId", long.class, Visibility.PUBLIC)                    
                    .intercept(FieldAccessor.ofField("microhooksSourceId"))
                    .defineMethod("setMicrohooksSourceId", void.class, Visibility.PUBLIC)
                    .withParameters(long.class)
                    .intercept(FieldAccessor.ofField("microhooksSourceId"));
            }
            if (isCustomSink) {
                builder = builder.constructor(ElementMatchers.any())
                            .intercept(Advice.to(CustomSinkAdvisor.class));
            }
            return builder;
        } 
        if (annotations.contains("@io.microhooks.common.Dto")) {
            Class jsonIgnoreProperties = loader.findClass("com.fasterxml.jackson.annotation.JsonIgnoreProperties");
            builder = builder.annotateType(AnnotationDescription.Builder.ofType(jsonIgnoreProperties)
                            .define("ignoreUnknown", true).build());
        } else if (annotations.contains("@org.springframework.boot.autoconfigure.SpringBootApplication")) {
            Class springConfig = loader.findClass("io.microhooks.containers.spring.Config");
            if (springConfig != null) {
                Class importt = loader.findClass("org.springframework.context.annotation.Import");
                builder = builder.annotateType(AnnotationDescription.Builder.ofType(importt)
                        .defineTypeArray("value", new Class[]{springConfig}).build());
            } 
        }

        return builder;

    }

    @Override
    public void close() throws IOException {
    }

    private static class CustomSinkAdvisor {

        @Advice.OnMethodExit
        public static void enter(@Advice.This Object customSink) throws Exception {
            Method method = Class.forName("io.microhooks.internal.Context")
                                .getDeclaredMethod("registerCustomSink", Object.class);
            method.invoke(null, customSink);
        }
    }
}
