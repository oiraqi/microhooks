package io.microhooks.tests.spring.raw.monitor;

public class Monitor {
    public static int sourceCount = 0;
    public static long sourceTotalTime = 0;
    public static long sourceMinTime = 1000000000;
    public static long sourceMaxTime = 0;

    public static int customCount = 0;
    public static long customTotalTime = 0;
    public static long customMinTime = 1000000000;
    public static long customMaxTime = 0;
}
