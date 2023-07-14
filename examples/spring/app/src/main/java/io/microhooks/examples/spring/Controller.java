package io.microhooks.examples.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    SourceRepository sourceRepo;
    
    @GetMapping("/hello")
    public String sayHello() {
        SourceEntity entity = new SourceEntity();
        entity.setName("Hi!");
        entity.setAmount(10);
        entity = sourceRepo.save(entity);

        entity.setName("Hello");
        entity = sourceRepo.save(entity);

        entity.setName("Hello world!");
        entity.setName("Hello again!");
        sourceRepo.save(entity);
        
        return "Hello!";
    }
}
