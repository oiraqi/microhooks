package io.microhooks.internal.util;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.Id;

import org.atteo.classindex.ClassIndex;

import io.microhooks.internal.IdNotFoundException;
import io.microhooks.sink.CustomSink;
import io.microhooks.sink.ProcessEvent;

public class CachingReflector {

    // We use ConcurrentHashMap here for thread safety without sacrificing
    // performance

    // A cache for entity class Id names, so that they are extracted through
    // refflection only once per entity class for better performance
    private static final Map<String, String> IDMAP = new ConcurrentHashMap<>();

    // A cache for reflected stream/Projection mappings so that reflection is performed
    // only once per Source, entity class (for all its instances)
    private static Map<String, Map<String, Class<?>>> SOURCE_MAP;

    private static Set<String> SOURCE_STREAMS;


    // We use Vector here for thread safety
    // A cache for reflected fields so that reflection is performed only
    // once per Trackable entity class (for all its instances)
    private static Map<String, Set<String>> TRACKED_FIELDS_NAMES;

    private static final Map<String, List<Method>> PRODUCE_EVENT_ON_CREATE_METHODS = new HashMap<>();
    private static final Map<String, List<Method>> PRODUCE_EVENT_ON_UPDATE_METHODS = new HashMap<>();
    private static final Map<String, List<Method>> PRODUCE_EVENT_ON_DELETE_METHODS = new HashMap<>();

    private static final Map<String, List<Method>> PRODUCE_EVENTS_ON_CREATE_METHODS = new HashMap<>();
    private static final Map<String, List<Method>> PRODUCE_EVENTS_ON_UPDATE_METHODS = new HashMap<>();
    private static final Map<String, List<Method>> PRODUCE_EVENTS_ON_DELETE_METHODS = new HashMap<>();

    private static Map<String, ArrayList<Class<?>>> SINK_MAP; // <stream -- entityClasses>
    private static final Map<String, ArrayList<Object>> CUSTOM_SINK_MAP = new HashMap<>(); // <stream -- [sink1, sink2, ...]>>
    private static final Map<String, ArrayList<String>> REGISTERED_CUSTOM_SINK_CLASSES = new HashMap<>(); // <class - [stream1, stream2, ...]>
    private static final Map<String, Map<Method, String>> PROCESS_EVENT_METHODS = new HashMap<>(); // <stream#className -- [<m1, label1>, <m2, label2>]>
    private static final Set<String> CUSTOM_SINK_STREAMS = new HashSet<>();

    private CachingReflector() {
    }

    public static void init() {
        loadSourceMap();
        loadOnMethods();
        loadSinkMap();
        registerCustomSinkClasses();
    }

    public static Object getFieldValue(Object instance, String fieldName) throws Exception {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.getName().equals("get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1))) {
                return method.invoke(instance);
            }
        }
        return null;
    }

    public static long getId(Object entity) throws Exception {
        Class<?> entityClass = entity.getClass();
        String entityClassName = entityClass.getName();

        if (!IDMAP.containsKey(entityClassName)) {
            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class)) {                    
                    IDMAP.put(entityClassName, field.getName());
                    break;
                }
            }
        }

        if (!IDMAP.containsKey(entityClassName)) {
            throw new IdNotFoundException();
        }
        return ((Long) CachingReflector.getFieldValue(entity, IDMAP.get(entityClassName)));

    }

    public static Map<String, Class<?>> getSourceMappings(Object sourceEntity) throws Exception {
        return SOURCE_MAP.get(sourceEntity.getClass().getName());
    }

    public static Set<String> getSourceStreams() {
        return SOURCE_STREAMS;
    }

    public static Set<String> getTrackedFieldsNames(Object customSourceEntity) {
        return TRACKED_FIELDS_NAMES.get(customSourceEntity.getClass().getName());
    }

    public static List<Method> getProduceEventOnCreateMethods(Object customSourceEntity) {
        List<Method> methods = PRODUCE_EVENT_ON_CREATE_METHODS.get(customSourceEntity.getClass().getName());
        return methods != null ? methods:new ArrayList<>();
    }

    public static List<Method> getProduceEventsOnCreateMethods(Object customSourceEntity) {
        List<Method> methods =  PRODUCE_EVENTS_ON_CREATE_METHODS.get(customSourceEntity.getClass().getName());
        return methods != null ? methods:new ArrayList<>();
    }

    public static List<Method> getProduceEventOnUpdateMethods(Object customSourceEntity) {
        List<Method> methods =  PRODUCE_EVENT_ON_UPDATE_METHODS.get(customSourceEntity.getClass().getName());
        return methods != null ? methods:new ArrayList<>();
    }

    public static List<Method> getProduceEventsOnUpdateMethods(Object customSourceEntity) {
        List<Method> methods =  PRODUCE_EVENTS_ON_UPDATE_METHODS.get(customSourceEntity.getClass().getName());
        return methods != null ? methods:new ArrayList<>();
    }

    public static List<Method> getProduceEventOnDeleteMethods(Object customSourceEntity) {
        List<Method> methods =  PRODUCE_EVENT_ON_DELETE_METHODS.get(customSourceEntity.getClass().getName());
        return methods != null ? methods:new ArrayList<>();
    }

    public static List<Method> getProduceEventsOnDeleteMethods(Object customSourceEntity) {
        List<Method> methods =  PRODUCE_EVENTS_ON_DELETE_METHODS.get(customSourceEntity.getClass().getName());
        return methods != null ? methods:new ArrayList<>();
    }

    public static ArrayList<Class<?>> getSinks(String stream) {
        return SINK_MAP.get(stream);
    }

    public static void registerCustomSink(Object customSink) {
        List<String> streams = REGISTERED_CUSTOM_SINK_CLASSES.get(customSink.getClass().getName());
        for (String stream : streams) {
            ArrayList<Object> customSinks = null;
            if (!CUSTOM_SINK_MAP.containsKey(stream)) {
                customSinks = new ArrayList<>();
                CUSTOM_SINK_MAP.put(stream, customSinks);
            } else {
                customSinks = CUSTOM_SINK_MAP.get(stream);
            }
            customSinks.add(customSink);
        }
    }

    public static Map<Method, String> getProcessEventMethodsToInvoke(String stream, Object customSink) {
        String key = stream + "#" + customSink.getClass().getName();
        return PROCESS_EVENT_METHODS.get(key);
    }

    public static boolean hasSinks() {
        return !SINK_MAP.isEmpty() || !CUSTOM_SINK_MAP.isEmpty();
    }

    public static Set<String> getSinkStreams() {
        return SINK_MAP.keySet();
    }

    public static Set<String> getCustomSinkStreams() {
        return CUSTOM_SINK_MAP.keySet();
    }

    public static Set<String> getAllStreams() {
        HashSet<String> set = new HashSet<>();
        for (String topic : SINK_MAP.keySet()) {
            set.add(topic);
        }
        for (String topic : CUSTOM_SINK_STREAMS) {
            set.add(topic);
        }
        return set;
    } 

    public static List<Object> getCustomSinks(String stream) {
        return CUSTOM_SINK_MAP.get(stream);
    }

    private static void loadSourceMap() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/source/sources.bin"))) {
            SOURCE_MAP = (Map<String, Map<String, Class<?>>>)in.readObject();
        } catch (Exception ex) {
            SOURCE_MAP = new HashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/source/streams.bin"))) {
            SOURCE_STREAMS = (Set<String>)in.readObject();
        } catch (Exception ex) {
            SOURCE_STREAMS = new HashSet<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/source/tracked-fields-names.bin"))) {
            TRACKED_FIELDS_NAMES = (Map<String, Set<String>>)in.readObject();
        } catch (Exception ex) {
            TRACKED_FIELDS_NAMES = new HashMap<>();
        }
    }

    private static void loadOnMethods() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/source/produce-event-on-create-methods.bin"))) {
            Map<String, List<String>> map = (Map<String, List<String>>)in.readObject();
            for (Entry<String, List<String>> entry : map.entrySet()) {
                List<Method> methods = new ArrayList<>();
                for (String method : entry.getValue()) {
                    methods.add(Class.forName(entry.getKey()).getMethod(method));
                }
                PRODUCE_EVENT_ON_CREATE_METHODS.put(entry.getKey(), methods);                
            }
        } catch (Exception ex) {
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/source/produce-events-on-create-methods.bin"))) {
            Map<String, List<String>> map = (Map<String, List<String>>)in.readObject();
            for (Entry<String, List<String>> entry : map.entrySet()) {
                List<Method> methods = new ArrayList<>();
                for (String method : entry.getValue()) {
                    methods.add(Class.forName(entry.getKey()).getMethod(method));
                }
                PRODUCE_EVENTS_ON_CREATE_METHODS.put(entry.getKey(), methods);
                
            }
        } catch (Exception ex) {
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/source/produce-event-on-update-methods.bin"))) {
            Map<String, List<String>> map = (Map<String, List<String>>)in.readObject();
            for (Entry<String, List<String>> entry : map.entrySet()) {
                List<Method> methods = new ArrayList<>();
                for (String method : entry.getValue()) {
                    methods.add(Class.forName(entry.getKey()).getMethod(method, Map.class));
                }
                PRODUCE_EVENT_ON_UPDATE_METHODS.put(entry.getKey(), methods);
                
            }
        } catch (Exception ex) {
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/source/produce-events-on-update-methods.bin"))) {
            Map<String, List<String>> map = (Map<String, List<String>>)in.readObject();
            for (Entry<String, List<String>> entry : map.entrySet()) {
                List<Method> methods = new ArrayList<>();
                for (String method : entry.getValue()) {
                    methods.add(Class.forName(entry.getKey()).getMethod(method));
                }
                PRODUCE_EVENTS_ON_UPDATE_METHODS.put(entry.getKey(), methods);
                
            }
        } catch (Exception ex) {
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/source/produce-event-on-uelete-methods.bin"))) {
            Map<String, List<String>> map = (Map<String, List<String>>)in.readObject();
            for (Entry<String, List<String>> entry : map.entrySet()) {
                List<Method> methods = new ArrayList<>();
                for (String method : entry.getValue()) {
                    methods.add(Class.forName(entry.getKey()).getMethod(method));
                }
                PRODUCE_EVENT_ON_DELETE_METHODS.put(entry.getKey(), methods);
                
            }
        } catch (Exception ex) {
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/source/produce-events-on-uelete-methods.bin"))) {
            Map<String, List<String>> map = (Map<String, List<String>>)in.readObject();
            for (Entry<String, List<String>> entry : map.entrySet()) {
                List<Method> methods = new ArrayList<>();
                for (String method : entry.getValue()) {
                    methods.add(Class.forName(entry.getKey()).getMethod(method));
                }
                PRODUCE_EVENTS_ON_DELETE_METHODS.put(entry.getKey(), methods);
                
            }
        } catch (Exception ex) {
        }
    }

    private static void loadSinkMap() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/resources/store/sink/sinks.bin"))) {
            SINK_MAP = (Map<String, ArrayList<Class<?>>>)in.readObject();
        } catch (Exception ex) {
            SINK_MAP = new HashMap<>();
        }
    }


    private static void registerCustomSinkClasses() {
        Iterable<Class<?>> customSinks = ClassIndex.getAnnotated(CustomSink.class);
        for (Class<?> customSink : customSinks) {
            ArrayList<String> streams = new ArrayList<>();
            for (Method method : customSink.getDeclaredMethods()) {
                ProcessEvent pe = null;
                if ((pe = method.getAnnotation(ProcessEvent.class)) != null) {
                    String stream = pe.stream();
                    if (!CUSTOM_SINK_STREAMS.contains(stream)) {
                        CUSTOM_SINK_STREAMS.add(stream);
                    }
                    String label = pe.label();
                    String key = stream + "#" + customSink.getName();
                    if (!streams.contains(stream)) {
                        streams.add(stream);
                    }
                    Map<Method, String> methods = null;
                    if (!PROCESS_EVENT_METHODS.containsKey(key)) {
                        methods = new HashMap<>();
                        PROCESS_EVENT_METHODS.put(key, methods);
                    } else {
                        methods = PROCESS_EVENT_METHODS.get(key);
                    }
                    methods.put(method, label);
                }
            }
            REGISTERED_CUSTOM_SINK_CLASSES.put(customSink.getName(), streams);
        }
    }
}
