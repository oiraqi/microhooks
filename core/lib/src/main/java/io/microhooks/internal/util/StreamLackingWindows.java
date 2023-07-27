package io.microhooks.internal.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.microhooks.internal.Context;
import lombok.Getter;
import lombok.Setter;

public class StreamLackingWindows {

    private static final String STREAMS_FILE = "src/main/resources/META-INF/streams.bin";
    private static Map<String, LackingWindow> map;

    public static void init() throws Exception {
        load();
        Set<String> streams = Context.getSourceStreams();
        for (String stream : streams) {
            if (!exists(stream)) {
                addStream(stream);
            }
        }
        save();
    }

    private static void addStream(String stream) throws Exception {
        map.put(stream, new LackingWindow());
        save();
    }

    private static boolean exists(String stream) {
        return map.containsKey(stream);
    }

    private static boolean isOpen(String stream) {
        return map.containsKey(stream) && map.get(stream).isOpen();
    }

    private static long getStartTime(String stream) {
        return map.get(stream).getStartTime();
    }

    private static long getEndTime(String stream) {
        return map.get(stream).getEndTime();
    }

    private static void setStartTime(String stream, long startTime) throws Exception {
        map.get(stream).setStartTime(startTime);
        save();
    }

    private static void load() throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(STREAMS_FILE))) {
            map = (Map<String, LackingWindow>) in.readObject();
        } catch (Exception e) {
            map = new HashMap<>();
        }

    }

    private static void save() throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(STREAMS_FILE));
        out.writeObject(map);
    }

    private static class LackingWindow implements Serializable {
        @Getter
        @Setter
        private long startTime = 0;

        @Getter
        private long endTime = new Date().getTime();

        @Getter
        @Setter
        private boolean open = true;
    }
}
