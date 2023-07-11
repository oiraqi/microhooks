package io.microhooks.core.internal.util;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerdes {
    //Thread-safe, as long as we don't call setConfig and setDateFormat
    final ObjectMapper mapper = new ObjectMapper();
    private static JsonSerdes singleton = null;

    private JsonSerdes() {
        //Don't let anyone instantiate me!
    }

    public static JsonSerdes getSingleton() {
        if (singleton == null) {
            singleton = new JsonSerdes();
        }
        return singleton;
    }

    public byte[] serialize(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    public Object deserialize(byte[] bytes, Class<?> clazz) throws IOException {
        return mapper.readValue(bytes, clazz);
    }
}
