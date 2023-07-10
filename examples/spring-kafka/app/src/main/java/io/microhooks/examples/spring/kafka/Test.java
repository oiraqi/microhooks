/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.microhooks.examples.spring.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.microhooks.consumer.CustomSink;
import io.microhooks.consumer.ProcessEvent;
import io.microhooks.core.Event;

@SpringBootApplication
@RestController
@CustomSink
@Configuration
@Import(io.microhooks.extensions.environments.spring.Config.class)
public class Test {

    @Autowired
    TestRepository repo;
    
    public static void main(String[] args) {
        SpringApplication.run(Test.class, args);
    }

    @GetMapping("/hello")
    public String sayHello() {
        TestEntity entity = new TestEntity();
        entity.setName("Hi!");
        entity = repo.save(entity);

        entity.setName("Hello");
        entity = repo.save(entity);

        entity.setName("Hello world!");
        entity.setName("Hello again!");
        repo.save(entity);
        
        return "Hello!";
    }

    @ProcessEvent(streams="CustomStream", label="NameChanged")
    public void processEvent(long key, Event<String> event) {
        System.out.println("Received Event Key: " + key);
        System.out.println("Received Event Timestamp: " + event.getTimestamp());
        // System.out.println("Received Event Username: " + event.getUsername());
        System.out.println("Received Event Payload: " + event.getPayload());        
    }
    
}
