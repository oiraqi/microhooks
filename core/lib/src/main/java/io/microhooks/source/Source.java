package io.microhooks.source;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Source {
    // stream:projection, e.g., {"stream1:projection1", "stream2:projection2"}
    String[] mappings();
}
