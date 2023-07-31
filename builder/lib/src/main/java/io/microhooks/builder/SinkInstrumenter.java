package io.microhooks.builder;

import java.lang.reflect.Method;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.matcher.ElementMatchers;

public class SinkInstrumenter {

    public static DynamicType.Builder<?> process(DynamicType.Builder<?> builder, Loader loader) {
        Class sinkable = loader.findClass("io.microhooks.internal.Sinkable");
            Class column = loader.findClass("jakarta.persistence.Column");
            return builder.implement(sinkable)
                    .defineField("microhooksSourceId", long.class, Visibility.PRIVATE)
                    .annotateField(AnnotationDescription.Builder.ofType(column).define("unique", true).build())
                    .defineMethod("getMicrohooksSourceId", long.class, Visibility.PUBLIC)
                    .intercept(FieldAccessor.ofField("microhooksSourceId"))
                    .defineMethod("setMicrohooksSourceId", void.class, Visibility.PUBLIC)
                    .withParameters(long.class)
                    .intercept(FieldAccessor.ofField("microhooksSourceId"));
    }

    public static DynamicType.Builder<?> processCustom(DynamicType.Builder<?> builder) {
        return builder.constructor(ElementMatchers.any()).intercept(Advice.to(CustomSinkAdvisor.class));
    }

    public static DynamicType.Builder<?> processProjection(DynamicType.Builder<?> builder, Loader loader) {
        Class jsonIgnoreProperties = loader.findClass("com.fasterxml.jackson.annotation.JsonIgnoreProperties");
        return builder.annotateType(AnnotationDescription.Builder.ofType(jsonIgnoreProperties)
                    .define("ignoreUnknown", true).build());
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
