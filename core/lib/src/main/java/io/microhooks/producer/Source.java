package io.microhooks.producer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Source {
    // stream:dto:[add owner to event or not], e.g., {"stream1:dto1:y", "stream2:dto2:n"}
    String[] mappings();
}
