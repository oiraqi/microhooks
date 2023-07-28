package io.microhooks.examples.micronaut;

import java.util.Iterator;
import java.util.List;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.scheduling.TaskExecutors;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@ExecuteOn(TaskExecutors.IO)
@Controller("/sinks")
public class SinkController {

    @PersistenceContext
    EntityManager em;

    @Inject
    SinkService sinkService;
    
    @Get
    public String list() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        TypedQuery<SinkEntity> query = em.createQuery("SELECT s FROM SinkEntity as s", SinkEntity.class);
        List<SinkEntity> sinks = query.getResultList();
        tx.commit();
        Iterator<SinkEntity> sinkIterator = sinks.iterator();
        StringBuffer response = new StringBuffer();
        while (sinkIterator.hasNext()) {
            response.append(sinkIterator.next() + "\n");
        }
        System.out.println(response);
        return response.toString();
    }
}
