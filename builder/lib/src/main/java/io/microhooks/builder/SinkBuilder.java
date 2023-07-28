package io.microhooks.builder;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.annotation.AnnotationDescription;

public class SinkBuilder {
    private static final Map<String, List<String>> SINK_MAP = new HashMap<>(); // <stream -- entityClasses>
    private static final Map<String, List<String>> REGISTERED_CUSTOM_SINK_CLASSES = new HashMap<>(); // <class - [stream1, stream2, ...]>
    private static final Map<String, Map<String, String>> PROCESS_EVENT_METHODS = new HashMap<>(); // <stream#className -- [<m1, label1>, <m2, label2>]>
    private static final Set<String> CUSTOM_SINK_STREAMS = new HashSet<>();

    private static Class sink = null;
    private static Class processEvent = null;

    public static void processSink(TypeDescription target, Loader loader) {
        if (sink == null) {
            sink = loader.findClass("io.microhooks.sink.Sink");
        }
        String stream = (String)target.getDeclaredAnnotations().ofType(sink).getValue("stream").resolve();
        if (SINK_MAP.containsKey(stream)) {
            SINK_MAP.get(stream).add(target.getActualName());
        } else {
            List<String> list = new ArrayList<>();
            list.add(target.getActualName());
            SINK_MAP.put(stream, list);
        }
    }

    public static void processCustomSink(TypeDescription target, Loader loader) {
        if (processEvent == null) {
            processEvent = loader.findClass("io.microhooks.sink.ProcessEvent");
        }
        final ArrayList<String> streams = new ArrayList<>();
        target.getDeclaredMethods().forEach(method -> {
            AnnotationDescription annotation = method.getDeclaredAnnotations().ofType(processEvent);
            if (annotation != null) {
                String stream = annotation.getValue("stream").resolve(String.class);
                String label = annotation.getValue("label").resolve(String.class);
                if (!CUSTOM_SINK_STREAMS.contains(stream)) {
                    CUSTOM_SINK_STREAMS.add(stream);
                }                    
                if (!streams.contains(stream)) {
                    streams.add(stream);
                }
                String key = stream + "#" + target.getActualName();
                Map<String, String> methods = null;
                if (!PROCESS_EVENT_METHODS.containsKey(key)) {
                    methods = new HashMap<>();
                    PROCESS_EVENT_METHODS.put(key, methods);
                } else {
                    methods = PROCESS_EVENT_METHODS.get(key);
                }
                methods.put(method.getActualName(), label);
            }
        });
        
        REGISTERED_CUSTOM_SINK_CLASSES.put(target.getActualName(), streams);
    }

    public static void save() throws IOException {
        String path = ".microhooks/";
        if (new File("app/").exists()) {
            path = "app/.microhooks/";
        }
        if (!new File(path).exists()) {
            new File(path).mkdir();
            new File(path + "sink").mkdir();
        } else if (new File(path).exists() && !new File(path + "sink").exists()) {
            new File(path + "sink").mkdir();
        }
        path += "sink/";
        
        if (!SINK_MAP.isEmpty()) {
           try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "sinks.bin"))) {
                out.writeObject(SINK_MAP);
            }
        }

        if (!CUSTOM_SINK_STREAMS.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "custom-sink-streams.bin"))) {
                out.writeObject(CUSTOM_SINK_STREAMS);
            }
        }

        if (!PROCESS_EVENT_METHODS.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "process-event-methods.bin"))) {
                out.writeObject(PROCESS_EVENT_METHODS);
            }
        }

        if (!REGISTERED_CUSTOM_SINK_CLASSES.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "registered-custom-sink-classes.bin"))) {
                out.writeObject(REGISTERED_CUSTOM_SINK_CLASSES);
            }
        }
        
    }
}
