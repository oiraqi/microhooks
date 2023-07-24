package io.microhooks.containers.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.runtime.Startup;

import io.microhooks.internal.ApplicationBootstrap;
import io.microhooks.internal.SinkRepository;

@ApplicationScoped
@Startup
public class QuarkusApplicationBootstrap extends ApplicationBootstrap {

    @Inject
    SinkRepository sinkRepository;

    public QuarkusApplicationBootstrap() throws Exception {
        System.out.println("Hello from Quarkus");
        super.setup(sinkRepository);
    }
}
