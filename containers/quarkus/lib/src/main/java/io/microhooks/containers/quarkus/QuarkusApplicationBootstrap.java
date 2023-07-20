package io.microhooks.containers.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.runtime.Startup;

import io.microhooks.core.internal.ApplicationBootstrap;
import io.microhooks.core.internal.SinkRepository;

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
