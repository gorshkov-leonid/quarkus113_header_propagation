package org.maybe.rest;

import org.jboss.resteasy.core.ResteasyContext;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.concurrent.*;

@ApplicationScoped
@Path("/someone")
public class SomeoneElseMaybeResource
{
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/maybe")
    public CompletionStage<String> someoneElseYes()
    {
        List<String> authHeader = ResteasyContext.getContextData(HttpHeaders.class).getRequestHeader("Authorization");
        if (authHeader.isEmpty())
        {
            return CompletableFuture.completedFuture("no");
        }
        else
        {
            return CompletableFuture.completedFuture("yes");
        }
    }
}