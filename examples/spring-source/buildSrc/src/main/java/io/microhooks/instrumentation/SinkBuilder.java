package io.microhooks.instrumentation;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.annotation.AnnotationDescription;

public class SinkBuilder {
    private static final Map<String, List<Class<?>>> SINK_MAP = new HashMap<>(); // <stream -- entityClasses>
    private static final Map<String, ArrayList<Object>> CUSTOM_SINK_MAP = new HashMap<>(); // <stream -- [sink1, sink2, ...]>>
    private static final Map<String, ArrayList<String>> REGISTERED_CUSTOM_SINK_CLASSES = new HashMap<>(); // <class - [stream1, stream2, ...]>
    private static final Map<String, Map<String, String>> PROCESS_EVENT_METHODS = new HashMap<>(); // <stream#className -- [<m1, label1>, <m2, label2>]>
    private static final Set<String> CUSTOM_SINK_STREAMS = new HashSet<>();

    private static final String REF_FOLDER = "src/main/resources/";
    private static Class sink = null;
    private static Class processEvent = null;

    public static void processSink(TypeDescription target, Loader loader) {
        Map<String, Class<?>> map = new HashMap<>();
        if (sink == null) {
            sink = loader.findClass("io.microhooks.sink.Sink");
        }
        String stream = (String)target.getDeclaredAnnotations().ofType(sink).getValue("stream").resolve();
        if (SINK_MAP.containsKey(stream)) {
            SINK_MAP.get(stream).add(sink);
        } else {
            List<Class<?>> list = new ArrayList<>();
            list.add(sink);
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
        String path = REF_FOLDER;
        if (!new File(REF_FOLDER).exists()) {
            path = "app/" + REF_FOLDER;
        }
        if (!new File(path + "/store").exists()) {
            new File(path + "/store").mkdir();
            new File(path + "/store/source").mkdir();
            new File(path + "/store/sink").mkdir();
        }
        System.out.println(SINK_MAP);
        if (!SINK_MAP.isEmpty()) {
           try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "store/sink/sinks.bin"))) {
                out.writeObject(SINK_MAP);
            }
        }

        if (!CUSTOM_SINK_STREAMS.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "store/sink/custom-sink-streams.bin"))) {
                out.writeObject(CUSTOM_SINK_STREAMS);
            }
        }

        if (!PROCESS_EVENT_METHODS.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "store/sink/process-event-methods.bin"))) {
                out.writeObject(PROCESS_EVENT_METHODS);
            }
        }

        if (!REGISTERED_CUSTOM_SINK_CLASSES.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "store/sink/registered-custom-sink-classes.bin"))) {
                out.writeObject(REGISTERED_CUSTOM_SINK_CLASSES);
            }
        }
        
    }
}
