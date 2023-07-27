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


import java.lang.reflect.Method;
import java.lang.reflect.Field;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.annotation.AnnotationList;


public class SourceBuilder {
    private static final Map<String, Map<String, Class<?>>> SOURCE_MAP = new HashMap<>();
    private static final Set<String> SOURCE_STREAMS = new HashSet<>();
    
    private static final Map<String, Set<String>> TRACKED_FIELDS_NAMES = new HashMap<>();
    private static final Map<String, List<String>> PRODUCE_EVENT_ON_CREATE_METHODS = new HashMap<>();
    private static final Map<String, List<String>> PRODUCE_EVENT_ON_UPDATE_METHODS = new HashMap<>();
    private static final Map<String, List<String>> PRODUCE_EVENT_ON_DELETE_METHODS = new HashMap<>();

    private static final Map<String, List<String>> PRODUCE_EVENTS_ON_CREATE_METHODS = new HashMap<>();
    private static final Map<String, List<String>> PRODUCE_EVENTS_ON_UPDATE_METHODS = new HashMap<>();
    private static final Map<String, List<String>> PRODUCE_EVENTS_ON_DELETE_METHODS = new HashMap<>();

    private static final String REF_FOLDER = "src/main/resources/";

    private static Class source = null;
    private static Class produceEventOnCreate = null;
    private static Class produceEventsOnCreate = null;
    private static Class produceEventOnUpdate = null;
    private static Class produceEventsOnUpdate = null;
    private static Class produceEventOnDelete = null;
    private static Class produceEventsOnDelete = null;

    public static void processSource(TypeDescription target, Loader loader) {
        Map<String, Class<?>> map = new HashMap<>();
        if (source == null) {
            source = loader.findClass("io.microhooks.source.Source");
        }
        String[] mappings = (String[])target.getDeclaredAnnotations().ofType(source).getValue("mappings").resolve();
        Set<String> filedNames = TRACKED_FIELDS_NAMES.get(target.getActualName());
        if (filedNames == null) {
            filedNames = new HashSet<>();
            TRACKED_FIELDS_NAMES.put(target.getActualName(), filedNames);
        }
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
                
                for (Field field : projectionClass.getDeclaredFields()) {
                    if (!filedNames.contains(field.getName())) {
                        filedNames.add(field.getName());
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SOURCE_MAP.put(target.getActualName(), map);
    }

    public static void processCustomSource(TypeDescription target, Loader loader) {
        
        parseTrackedFields(target, loader);

        if (produceEventOnCreate == null) {
            produceEventOnCreate = loader.findClass("io.microhooks.source.ProduceEventOnCreate");
            produceEventsOnCreate = loader.findClass("io.microhooks.source.ProduceEventsOnCreate");
            produceEventOnUpdate = loader.findClass("io.microhooks.source.ProduceEventOnUpdate");
            produceEventsOnUpdate = loader.findClass("io.microhooks.source.ProduceEventsOnUpdate");
            produceEventOnDelete = loader.findClass("io.microhooks.source.ProduceEventOnDelete");
            produceEventsOnDelete = loader.findClass("io.microhooks.source.ProduceEventsOnDelete");
        }
        final List<String> produceEventOnCreateMethods = new ArrayList<>();
        final List<String> produceEventsOnCreateMethods = new ArrayList<>();
        final List<String> produceEventOnUpdateMethods = new ArrayList<>();
        final List<String> produceEventsOnUpdateMethods = new ArrayList<>();
        final List<String> produceEventOnDeleteMethods = new ArrayList<>();
        final List<String> produceEventsOnDeleteMethods = new ArrayList<>();

        target.getDeclaredMethods().forEach((method) -> {
            AnnotationList annotationList = method.getDeclaredAnnotations();
            try {
                if (annotationList.isAnnotationPresent(produceEventOnCreate)) {                
                    produceEventOnCreateMethods.add(method.getName());
                } else if (annotationList.isAnnotationPresent(produceEventsOnCreate)) {                
                    produceEventsOnCreateMethods.add(method.getName());
                } else if (annotationList.isAnnotationPresent(produceEventOnUpdate)) {                
                    produceEventOnUpdateMethods.add(method.getName());
                } else if (annotationList.isAnnotationPresent(produceEventsOnUpdate)) {                
                    produceEventsOnUpdateMethods.add(method.getName());
                } else if (annotationList.isAnnotationPresent(produceEventOnDelete)) {                
                    produceEventOnDeleteMethods.add(method.getName());
                } else if (annotationList.isAnnotationPresent(produceEventsOnDelete)) {                
                    produceEventsOnDeleteMethods.add(method.getName());
                }
            } catch (Exception e) {
            }
        });
        if (!produceEventOnCreateMethods.isEmpty()) {
            PRODUCE_EVENT_ON_CREATE_METHODS.put(target.getActualName(), produceEventOnCreateMethods);
        }
        if (!produceEventsOnCreateMethods.isEmpty()) {
            PRODUCE_EVENTS_ON_CREATE_METHODS.put(target.getActualName(), produceEventsOnCreateMethods);
        }
        if (!produceEventOnUpdateMethods.isEmpty()) {
            PRODUCE_EVENT_ON_UPDATE_METHODS.put(target.getActualName(), produceEventOnUpdateMethods);
        }
        if (!produceEventsOnUpdateMethods.isEmpty()) {
            PRODUCE_EVENTS_ON_UPDATE_METHODS.put(target.getActualName(), produceEventsOnUpdateMethods);
        }
        if (!produceEventOnDeleteMethods.isEmpty()) {
            PRODUCE_EVENT_ON_DELETE_METHODS.put(target.getActualName(), produceEventOnDeleteMethods);
        }
        if (!produceEventsOnDeleteMethods.isEmpty()) {
            PRODUCE_EVENTS_ON_DELETE_METHODS.put(target.getActualName(), produceEventsOnDeleteMethods);
        }
    }

    private static void parseTrackedFields(TypeDescription target, Loader loader) {
        Set<String> filedNames = TRACKED_FIELDS_NAMES.get(target.getActualName());
        if (filedNames == null) {
            filedNames = new HashSet<>();
            TRACKED_FIELDS_NAMES.put(target.getActualName(), filedNames);
        }
        Class track = loader.findClass("io.microhooks.source.Track");
        final Set<String> trackedFieldNames = filedNames;
        target.getDeclaredFields().forEach(field -> {
            if (field.getDeclaredAnnotations().isAnnotationPresent(track)) {
                if (!trackedFieldNames.contains(field.getName())) {
                    trackedFieldNames.add(field.getName());
                }                
            }
        });
        
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
        if (!PRODUCE_EVENT_ON_CREATE_METHODS.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/store/source/produce-event-on-create-methods.bin"))) {
                out.writeObject(PRODUCE_EVENT_ON_CREATE_METHODS);
            }
        }
        if (!PRODUCE_EVENTS_ON_CREATE_METHODS.isEmpty()) {
           try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/store/source/produce-events-on-create-methods.bin"))) {
                out.writeObject(PRODUCE_EVENTS_ON_CREATE_METHODS);
            }
        }
        if (!PRODUCE_EVENT_ON_UPDATE_METHODS.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/store/source/produce-event-on-update-methods.bin"))) {
                out.writeObject(PRODUCE_EVENT_ON_UPDATE_METHODS);
            }
        }
        if (!PRODUCE_EVENTS_ON_UPDATE_METHODS.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/store/source/produce-events-on-update-methods.bin"))) {
                out.writeObject(PRODUCE_EVENTS_ON_UPDATE_METHODS);
            }
        }
        if (!PRODUCE_EVENT_ON_DELETE_METHODS.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/store/source/produce-event-on-delete-methods.bin"))) {
                out.writeObject(PRODUCE_EVENT_ON_DELETE_METHODS);
            }
        }
        if (!PRODUCE_EVENTS_ON_DELETE_METHODS.isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/store/source/produce-events-on-delete-methods.bin"))) {
                out.writeObject(PRODUCE_EVENTS_ON_DELETE_METHODS);
            }
        }
    }
}
