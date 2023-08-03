package io.microhooks.builder;

import java.io.IOException;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.description.annotation.AnnotationDescription;

public class Builder implements net.bytebuddy.build.Plugin {

    @Override
    public boolean matches(TypeDescription target) {
        String annotations = target.getDeclaredAnnotations().toString();
        return annotations.contains("@io.microhooks.source.Source") ||
                annotations.contains("@io.microhooks.source.CustomSource") ||
                annotations.contains("@io.microhooks.sink.Sink") ||
                annotations.contains("@io.microhooks.sink.CustomSink") ||
                annotations.contains("@io.microhooks.source.Projection") ||
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

            if (isSource) {
                SourceContextBuilder.processSource(target, loader);
            }
            if (isCustomSource) {
                SourceContextBuilder.processCustomSource(target, loader);
            }

            return SourceInstrumenter.process(builder, loader, isSource, isCustomSource);
        }

        boolean isSink = annotations.contains("@io.microhooks.sink.Sink");
        boolean isCustomSink = annotations.contains("@io.microhooks.sink.CustomSink");
        if (isSink) {            
            SinkContextBuilder.processSink(target, loader);
            return SinkInstrumenter.process(builder, loader);
        }
        if (isCustomSink) {
            SinkContextBuilder.processCustomSink(target, loader);            
            return SinkInstrumenter.processCustom(builder);
        }

        if (annotations.contains("@io.microhooks.source.Projection")) {
            return SourceInstrumenter.processProjection(builder, loader);
        } 
        
        if (annotations.contains("@org.springframework.boot.autoconfigure.SpringBootApplication")) {
            Class springConfig = loader.findClass("io.microhooks.containers.spring.Config");
            if (springConfig != null) {
                Class importt = loader.findClass("org.springframework.context.annotation.Import");
                return builder.annotateType(AnnotationDescription.Builder.ofType(importt)
                        .defineTypeArray("value", new Class[] { springConfig }).build());
            }
        }

        return builder;

    }

    @Override
    public void close() throws IOException {
        SourceContextBuilder.save();
        SinkContextBuilder.save();
    }

}
