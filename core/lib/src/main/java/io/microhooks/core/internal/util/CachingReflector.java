package io.microhooks.core.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.Id;

import org.atteo.classindex.ClassIndex;

import io.microhooks.consumer.Sink;
import io.microhooks.core.ConfigOption;
import io.microhooks.core.internal.IdNotFoundException;
import io.microhooks.producer.OnCreate;
import io.microhooks.producer.OnDelete;
import io.microhooks.producer.OnUpdate;
import io.microhooks.producer.Source;
import io.microhooks.producer.Track;

public class CachingReflector {

    // We use ConcurrentHashMap here for thread safety without sacrificing
    // performance

    // A cache for entity class Id names, so that they are extracted through
    // refflection only once per entity class for better performance
    private static final Map<String, Long> IDMAP = new ConcurrentHashMap<>();

    // A cache for reflected stream/DTO mappings so that reflection is performed
    // only
    // once per Source, entity class (for all its instances)
    private static final Map<String, Map<String, Entry<Class<?>, Boolean>>> SOURCE_MAPPINGS = new ConcurrentHashMap<>();

    private static final Map<String, Boolean> SOURCE_SIGN_MAPPINGS = new ConcurrentHashMap<>();

    // We use Vector here for thread safety
    // A cache for reflected fields so that reflection is performed only
    // once per Trackable entity class (for all its instances)
    private static final Map<String, Vector<String>> TRACKED_FIELDS_NAMES = new ConcurrentHashMap<String, Vector<String>>();

    private static final Map<String, ArrayList<Method>> ON_CREATE_METHODS = new ConcurrentHashMap<>();
    private static final Map<String, ArrayList<Method>> ON_UPDATE_METHODS = new ConcurrentHashMap<>();
    private static final Map<String, ArrayList<Method>> ON_DELETE_METHODS = new ConcurrentHashMap<>();

    private static final Map<String, Map<Class<?>, String>> SINK_MAP = new HashMap<>(); // <stream -- <entityClass --
                                                                                        // authenticationKey>>
    // private static final Map<String, ArrayList<Class<?>>> customSinkMap;

    private CachingReflector() {
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

        if (IDMAP.containsKey(entityClassName)) {
            return IDMAP.get(entityClassName);
        }

        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                Long id = ((Long) CachingReflector.getFieldValue(entity, field.getName()));
                IDMAP.put(entityClassName, id);
                return id;
            }
        }

        throw new IdNotFoundException();
    }

    public static Map<String, Entry<Class<?>, Boolean>> getSourceMappings(Object sourceEntity) throws Exception {
        Class<?> sourceEntityClass = sourceEntity.getClass();
        String sourceEntityClassName = sourceEntityClass.getName();
        Map<String, Entry<Class<?>, Boolean>> mappings = null;
        if (!SOURCE_MAPPINGS.containsKey(sourceEntityClassName)) {
            mappings = new ConcurrentHashMap<>();
            Source source = sourceEntityClass.<Source>getAnnotation(Source.class);
            try {
                for (String mapping : source.mappings()) {
                    StringTokenizer strTok = new StringTokenizer(mapping, ":");
                    String stream = strTok.nextToken();
                    String dtoClassName = strTok.nextToken();
                    Class<?> dtoClass = Class.forName(dtoClassName);
                    boolean add = false;
                    if (strTok.hasMoreTokens()) {
                        String addOwnerToEvent = strTok.nextToken();
                        if (addOwnerToEvent.equals("y")) {
                            add = true;
                        }
                    } else {
                        add = Config.getAddOwnerToEvent();
                    }

                    mappings.put(stream, new AbstractMap.SimpleEntry<>(dtoClass, add));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SOURCE_MAPPINGS.put(sourceEntityClassName, mappings);
        } else {
            mappings = SOURCE_MAPPINGS.get(sourceEntityClassName);
        }

        return mappings;
    }

    public static boolean getSign(Object sourceEntity) {
        Class<?> sourceEntityClass = sourceEntity.getClass();
        String sourceEntityClassName = sourceEntityClass.getName();
        if (!SOURCE_SIGN_MAPPINGS.containsKey(sourceEntityClassName)) {
            Source source = sourceEntityClass.<Source>getAnnotation(Source.class);
            SOURCE_SIGN_MAPPINGS.put(
                    sourceEntityClassName, source.sign() == ConfigOption.ENABLED ||
                            (source.sign() == ConfigOption.APP && Config.getSign()));
        }
        return SOURCE_SIGN_MAPPINGS.get(sourceEntityClassName);
    }

    public static Vector<String> getTrackedFields(Object customSourceEntity) {
        Class<?> customSourceEntityClass = (Class<?>) customSourceEntity.getClass();
        String customSourceEntityClassName = customSourceEntityClass.getName();

        if (!TRACKED_FIELDS_NAMES.containsKey(customSourceEntityClassName)) {
            Vector<String> trackedFieldsNames = new Vector<>();
            Field[] fields = customSourceEntityClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Track.class)) {
                    trackedFieldsNames.add(field.getName());
                }
            }
            TRACKED_FIELDS_NAMES.put(customSourceEntityClassName, trackedFieldsNames);
        }

        return TRACKED_FIELDS_NAMES.get(customSourceEntityClassName);
    }

    public static ArrayList<Method> getOnCreateMethods(Object customSourceEntity) {
        Class<?> customSourceEntityClass = customSourceEntity.getClass();
        String customSourceEntityClassName = customSourceEntityClass.getName();
        if (!ON_CREATE_METHODS.containsKey(customSourceEntityClassName)) {
            ArrayList<Method> onCreateMethods = new ArrayList<>();
            for (Method method : customSourceEntityClass.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(OnCreate.class)) {
                    onCreateMethods.add(method);
                }
            }
            ON_CREATE_METHODS.put(customSourceEntityClassName, onCreateMethods);
        }
        return ON_CREATE_METHODS.get(customSourceEntityClassName);
    }

    public static ArrayList<Method> getOnUpdateMethods(Object customSourceEntity) {
        Class<?> customSourceEntityClass = customSourceEntity.getClass();
        String customSourceEntityClassName = customSourceEntityClass.getName();
        if (!ON_UPDATE_METHODS.containsKey(customSourceEntityClassName)) {
            ArrayList<Method> onUpdateMethods = new ArrayList<>();
            for (Method method : customSourceEntityClass.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(OnUpdate.class)) {
                    onUpdateMethods.add(method);
                }
            }
            ON_UPDATE_METHODS.put(customSourceEntityClassName, onUpdateMethods);
        }
        return ON_UPDATE_METHODS.get(customSourceEntityClassName);
    }

    public static ArrayList<Method> getOnDeleteMethods(Object customSourceEntity) {
        Class<?> customSourceEntityClass = customSourceEntity.getClass();
        String customSourceEntityClassName = customSourceEntityClass.getName();
        if (!ON_DELETE_METHODS.containsKey(customSourceEntityClassName)) {
            ArrayList<Method> onDeleteMethods = new ArrayList<>();
            for (Method method : customSourceEntityClass.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(OnDelete.class)) {
                    onDeleteMethods.add(method);
                }
            }
            ON_DELETE_METHODS.put(customSourceEntityClassName, onDeleteMethods);
        }
        return ON_DELETE_METHODS.get(customSourceEntityClassName);
    }

    public static Map<String, Map<Class<?>, String>> getSinkMap() {
        if (SINK_MAP.isEmpty()) {
            buildSinkMap();
        }
        return SINK_MAP;
    }

    private static void buildSinkMap() {
        Iterable<Class<?>> sinks = ClassIndex.getAnnotated(Sink.class);
        for (Class<?> sink : sinks) {
            Sink sinkAnnotation = sink.<Sink>getAnnotation(Sink.class);
            String stream = sinkAnnotation.stream();
            ConfigOption authenticateOption = sinkAnnotation.authenticate();
            String key = "";
            if (authenticateOption == ConfigOption.ENABLED ||
                    (authenticateOption == ConfigOption.APP && Config.getAuthenticate())) {
                key = sinkAnnotation.authenticationKey();
                if (key.equals("")) {
                    key = Config.getAuthenticationKey();
                }
            }
            if (SINK_MAP.containsKey(stream)) {
                SINK_MAP.get(stream).put(sink, key);
            } else {
                Map<Class<?>, String> map = new HashMap<>();
                map.put(sink, key);
                SINK_MAP.put(stream, map);
            }
        }
    }

    private static void buildCustomSinkMap() {

        /*
         * customSinkMap = new HashMap<>();
         * Iterable<Class<?>> customSinks = ClassIndex.getAnnotated(CustomSink.class);
         * for (Class<?> customSink : customSinks) {
         * CustomSink customSinkAnnotation =
         * customSink.<CustomSink>getAnnotation(CustomSink.class);
         * String stream = customSinkAnnotation.stream();
         * if (customSinkMap.containsKey(stream)) {
         * customSinkMap.get(stream).add(customSink);
         * } else {
         * ArrayList list = new ArrayList();
         * list.add(customSink);
         * customSinkMap.put(stream, list);
         * }
         * }
         */
    }
}
