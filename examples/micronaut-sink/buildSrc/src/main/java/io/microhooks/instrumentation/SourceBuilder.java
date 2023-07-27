package io.microhooks.instrumentation;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import net.bytebuddy.description.type.TypeDescription;


public class SourceBuilder {
    private static final Map<String, Map<String, Class<?>>> SOURCE_MAP = new HashMap<>();
    private static final Set<String> SOURCE_STREAMS = new HashSet<>();
    private static final Map<String, Set<String>> TRACKED_FIELDS_NAMES = new HashMap<>();
    private static final String REF_FOLDER = "src/main/resources/";
    private static Class sourceClazz = null;

    public static void build(TypeDescription target, Loader loader) {
        Map<String, Class<?>> map = new HashMap<>();
        if (sourceClazz == null) {
            sourceClazz = loader.findClass("io.microhooks.source.Source");
        }
        String[] mappings = (String[])target.getDeclaredAnnotations().ofType(sourceClazz).getValue("mappings").resolve();
        try {
            for (String mapping : mappings) {
                StringTokenizer strTok = new StringTokenizer(mapping, ":");
                String stream = strTok.nextToken();
                String projection = strTok.nextToken();
                Class<?> projectionClass = loader.findClass(projection);
                map.put(stream, projectionClass);
                if (!SOURCE_STREAMS.contains(stream)) {
                    SOURCE_STREAMS.add(stream);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SOURCE_MAP.put(target.getActualName(), map);
        parseTrackedFields(target, loader);
    }

    private static void parseTrackedFields(TypeDescription target, Loader loader) {
        final Set<String> trackedFieldsNames = new HashSet<>();
        Class track = loader.findClass("io.microhooks.source.Track");
        target.getDeclaredFields().forEach(field -> {
            if (field.getDeclaredAnnotations().isAnnotationPresent(track)) {
                trackedFieldsNames.add(field.getName());
                System.out.println(field.getName());
            }
        });
        
        TRACKED_FIELDS_NAMES.put(target.getActualName(), trackedFieldsNames);
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
        System.out.println(SOURCE_MAP);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "store/source/sources.bin"))) {
            out.writeObject(SOURCE_MAP);
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/store/source/streams.bin"))) {
            out.writeObject(SOURCE_STREAMS);
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/store/source/tracked-fields-names.bin"))) {
            out.writeObject(TRACKED_FIELDS_NAMES);
        }
    }
}
