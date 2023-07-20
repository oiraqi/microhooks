package io.microhooks.examples.quarkus;

import java.util.List;
import java.util.Iterator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import jakarta.transaction.Transactional;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Path("hello")
public class SinkController {

    /*@Inject
    EntityManager em;*/
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String list() {
        /*EntityTransaction tx = em.getTransaction();
        tx.begin();*/
        //TypedQuery<SinkEntity> query = em.createQuery("SELECT s FROM SinkEntity as s", SinkEntity.class);
        //List<SinkEntity> sinks = query.getResultList();
        //tx.commit();
        /*Iterator<SinkEntity> sinkIterator = sinks.iterator();
        StringBuffer response = new StringBuffer();
        while (sinkIterator.hasNext()) {
            response.append(sinkIterator.next() + "\n");
        }
        return response.toString();*/
        return "hello";
    }
}
