package io.microhooks.instrumentation;

import java.io.IOException;
import java.util.Map;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.modifier.FieldPersistence;

public class GradlePlugin implements net.bytebuddy.build.Plugin {

    private static class Loader extends ClassLoader {
        public Class findClass(String name, ClassFileLocator classFileLocator) {
            byte[] bytes;
            try {
                bytes = classFileLocator.locate(name).resolve();
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean matches(TypeDescription target) {
        String annotations = target.getDeclaredAnnotations().toString();
        return annotations.contains("@io.microhooks.core.Microhooks") ||
                annotations.contains("@io.microhooks.producer.Source") ||
                annotations.contains("@io.microhooks.producer.CustomProducer") ||
                annotations.contains("@io.microhooks.consumer.CustomConsumer") ||
                annotations.contains("@io.microhooks.core.Dto");
    }

    @Override
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription target,
            ClassFileLocator classFileLocator) {

        String annotations = target.getDeclaredAnnotations().toString();
        boolean isSource = annotations.contains("@io.microhooks.producer.Source");
        boolean isCustomSource = annotations.contains("@io.microhooks.producer.CustomSource");
        boolean isCustomSink = annotations.contains("@io.microhooks.consumer.CustomSink");


        Loader loader = new Loader();       

        if (isSource || isCustomSource) {
            Class[] listeners = null;
            Class entityListeners = loader.findClass("javax.persistence.EntityListeners", classFileLocator);
            loader.findClass("io.microhooks.core.internal.Listener", classFileLocator);

            if (isCustomSource) {
                Class trackable = loader.findClass("io.microhooks.core.internal.Trackable", classFileLocator);
                Generic map = TypeDescription.Generic.Builder.parameterizedType(Map.class, String.class, Object.class)
                        .build();
                builder = builder.implement(trackable)
                        .defineField("microhooksTrackedFields", map, Visibility.PRIVATE, FieldPersistence.TRANSIENT)
                        .defineMethod("getMicrohooksTrackedFields", map, Visibility.PUBLIC)
                        .intercept(FieldAccessor.ofField("microhooksTrackedFields"))
                        .defineMethod("setMicrohooksTrackedFields", void.class, Visibility.PUBLIC)
                        .withParameters(map)
                        .intercept(FieldAccessor.ofField("microhooksTrackedFields"));
                
                Class customListener = loader.findClass("io.microhooks.core.internal.CustomListener", classFileLocator);
                
                if (isSource) {
                    Class sourceListener = loader.findClass("io.microhooks.core.internal.SourceListener", classFileLocator);
                    listeners = new Class[2];
                    listeners[0] = sourceListener;
                    listeners[1] = customListener;
                } else {
                    listeners = new Class[1];
                    listeners[0] = customListener;
                }
            } else {
                Class sourceListener = loader.findClass("io.microhooks.core.internal.SourceListener", classFileLocator);
                listeners = new Class[1];
                listeners[0] = sourceListener;
            }
            builder = builder.annotateType(AnnotationDescription.Builder.ofType(entityListeners)
                            .defineTypeArray("value", listeners).build());
        } else if (annotations.contains("@io.microhooks.core.Dto")) {
            Class jsonIgnoreProperties = loader.findClass("com.fasterxml.jackson.annotation.JsonIgnoreProperties",
                                                        classFileLocator);
            builder = builder.annotateType(AnnotationDescription.Builder.ofType(jsonIgnoreProperties)
                            .define("ignoreUnknown", true).build());
        } else if (annotations.contains("@io.microhooks.core.MicrohooksApplication")) {      
            System.out.println("Hi!");  
            try {
                Class microhooksApp = loader.findClass("io.microhooks.core.MicrohooksApplication", classFileLocator);
                String container = (String)microhooksApp.getMethod("container").invoke(target.getDeclaredAnnotations().ofType(microhooksApp).load());
                if (container.equals("spring")) {
                    Class importt = loader.findClass("org.springframework.context.annotation.Import", classFileLocator);
                    Class springConfig = loader.findClass("io.microhooks.containers.spring.Config", classFileLocator);
                    builder = builder.annotateType(AnnotationDescription.Builder.ofType(importt)
                            .defineTypeArray("value", new Class[]{springConfig}).build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }   
        }

        return builder;

    }

    @Override
    public void close() throws IOException {
    }

}
