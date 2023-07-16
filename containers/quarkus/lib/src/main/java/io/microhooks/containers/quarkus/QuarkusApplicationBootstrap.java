package io.microhooks.containers.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.Startup;

import io.microhooks.core.internal.ApplicationBootstrap;

@ApplicationScoped
@Startup
public class QuarkusApplicationBootstrap extends ApplicationBootstrap {

    public QuarkusApplicationBootstrap() throws Exception {
        System.out.println("Hello from Quarkus");
        super.setup();
    }
}
