package io.microhooks.instrumentation;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import java.lang.reflect.Method;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import net.bytebuddy.description.type.TypeDescription;


public class SinkBuilder {
    private static final Map<String, List<Class<?>>> SINK_MAP = new HashMap<>(); // <stream -- entityClasses>
    private static final Map<String, ArrayList<Object>> CUSTOM_SINK_MAP = new HashMap<>(); // <stream -- [sink1, sink2, ...]>>
    private static final Map<String, ArrayList<String>> REGISTERED_CUSTOM_SINK_CLASSES = new HashMap<>(); // <class - [stream1, stream2, ...]>
    private static final Map<String, Map<Method, String>> PROCESS_EVENT_METHODS = new HashMap<>(); // <stream#className -- [<m1, label1>, <m2, label2>]>
    private static final Set<String> CUSTOM_SINK_STREAMS = new HashSet<>();

    private static final String REF_FOLDER = "src/main/resources/";
    private static Class sinkClazz = null;

    public static void build(TypeDescription target, Loader loader) {
        Map<String, Class<?>> map = new HashMap<>();
        if (sinkClazz == null) {
            sinkClazz = loader.findClass("io.microhooks.sink.Sink");
        }
        String stream = (String)target.getDeclaredAnnotations().ofType(sinkClazz).getValue("stream").resolve();
        if (SINK_MAP.containsKey(stream)) {
            SINK_MAP.get(stream).add(sinkClazz);
        } else {
            List<Class<?>> list = new ArrayList<>();
            list.add(sinkClazz);
            SINK_MAP.put(stream, list);
        }
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
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "store/sink/sinks.bin"))) {
            out.writeObject(SINK_MAP);
        }
        
    }
}
