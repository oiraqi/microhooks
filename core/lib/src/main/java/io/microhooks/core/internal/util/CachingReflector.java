package io.microhooks.core.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;

import io.microhooks.core.internal.IdNotFoundException;
import io.microhooks.producer.OnCreate;
import io.microhooks.producer.OnDelete;
import io.microhooks.producer.OnUpdate;
import io.microhooks.producer.Source;
import io.microhooks.producer.Track;

public class CachingReflector {

    //We use ConcurrentHashMap here for thread safety without sacrificing performance

    //A cache for entity class Id names, so that they are extracted through
    //refflection only once per entity class for better performance
    private static final Map<String, Long> IDMAP = new ConcurrentHashMap<>();

    //A cache for reflected stream/DTO mappings so that reflection is performed only
    //once per Source, entity class (for all its instances)
    private static final Map<String, Map<String, Class<?>>> SOURCE_MAPPINGS = new ConcurrentHashMap<>();

    //We use Vector here for thread safety
    //A cache for reflected fields so that reflection is performed only
    //once per Trackable entity class (for all its instances)
    private static final Map<String, Vector<String>> TRACKED_FIELDS_NAMES = new ConcurrentHashMap<String, Vector<String>>();

    private static final Map<String, ArrayList<Method>> ON_CREATE_METHODS = new ConcurrentHashMap<>();
    private static final Map<String, ArrayList<Method>> ON_UPDATE_METHODS = new ConcurrentHashMap<>();
    private static final Map<String, ArrayList<Method>> ON_DELETE_METHODS = new ConcurrentHashMap<>();

    private CachingReflector() {}

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

    
    public static Map<String, Class<?>> getSourceMappings(Object sourceEntity) throws Exception {
        Class<?> sourceEntityClass = sourceEntity.getClass();
        String sourceEntityClassName = sourceEntityClass.getName();
        Map<String, Class<?>> mappings = null;
        if (!SOURCE_MAPPINGS.containsKey(sourceEntityClassName)) {
            mappings = new ConcurrentHashMap<>();
            Source source = sourceEntityClass.<Source>getAnnotation(Source.class);
            try {
                for (String mapping : source.mappings()) {
                    StringTokenizer strTok = new StringTokenizer(mapping, ":");
                    String stream = strTok.nextToken();
                    String dtoClassName = strTok.nextToken();
                    Class<?> dtoClass = Class.forName(dtoClassName);
                    mappings.put(stream, dtoClass);
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

    public static Vector<String> getTrackedFields(Object customSourceEntity) {
        Class<?> customSourceEntityClass = (Class<?>)customSourceEntity.getClass();
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

    
}