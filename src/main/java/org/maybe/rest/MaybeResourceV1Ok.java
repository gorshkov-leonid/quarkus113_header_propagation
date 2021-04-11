package org.maybe.rest;

import org.eclipse.microprofile.context.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.core.ResteasyContext;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.concurrent.*;

@Path("/v1-ok/maybe")
public class MaybeResourceV1Ok
{
    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor executorService;

    @Inject
    @RestClient
    SomeoneElseMaybeResourceClient someoneElseClient;

    /* case 1.1. Wrap only a root future, cf-client in the middle of the chain. This is ok. */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> maybeYesOrNo()
    {
        return threadContext.withContextCapture(
                someoneElseClient.maybeYesOrNot()
                        .thenComposeAsync(answerFromSomeone -> {
                                    if (answerFromSomeone.equals("no"))
                                    {
                                        return CompletableFuture.completedFuture("no");
                                    }

                                    List<String> authHeader = ResteasyContext.getContextData(HttpHeaders.class).getRequestHeader("Authorization");
                                    if (authHeader.isEmpty())
                                    {
                                        return CompletableFuture.completedFuture("no");
                                    }
                                    else
                                    {
                                        return CompletableFuture.completedFuture("yes");
                                    }
                                },
                                executorService
                        )
        );
    }
}