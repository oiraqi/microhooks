package io.microhooks.ddd.internal;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Flushing {

    @After("execution(void javax.persistence.EntityManager.flush())")
    public void postFlush() {
        System.out.println("Post Flush");
    }

}