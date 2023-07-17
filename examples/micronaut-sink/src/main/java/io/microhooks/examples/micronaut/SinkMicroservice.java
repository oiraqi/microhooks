/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.microhooks.examples.micronaut;

import io.microhooks.core.MicrohooksApplication;
import io.microhooks.core.ContainerType;

import io.micronaut.runtime.Micronaut;

@MicrohooksApplication(name="SinkMicroservice1", container=ContainerType.MICRONAUT)
public class SinkMicroservice {
    
    public static void main(String[] args) {
        Micronaut.run(SinkMicroservice.class, args);
    }
}