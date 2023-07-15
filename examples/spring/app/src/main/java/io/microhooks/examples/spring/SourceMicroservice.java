/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.microhooks.examples.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.microhooks.core.MicrohooksApplication;

@SpringBootApplication
@MicrohooksApplication(name="SourceMicroservice")
public class SourceMicroservice {
   
    public static void main(String[] args) {
        SpringApplication.run(SourceMicroservice.class, args);
    }
    
}
