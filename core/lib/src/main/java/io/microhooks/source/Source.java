package io.microhooks.source;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Source {
    // stream:projection:[add owner to event or not], e.g., {"stream1:projection1:y", "stream2:projection2:n"}
    String[] mappings();
}
