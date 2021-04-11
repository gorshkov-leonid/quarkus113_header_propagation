package org.maybe.rest;

import org.eclipse.microprofile.context.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.core.ResteasyContext;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.concurrent.*;

@Path("/v2-ok/maybe")
public class MaybeResourceV2Ok
{
    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor executorService;

    @Inject
    @RestClient
    SomeoneElseMaybeResourceClient someoneElseClient;

    /* case 2.1. Wrap only a root future, static "supplyAsync", cf-client in the middle of the chain. This is ok. */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> maybeYesOrNo()
    {
        return threadContext.withContextCapture(
                CompletableFuture.supplyAsync(() -> 42, executorService)
                        .thenComposeAsync(
                                integer -> someoneElseClient.maybeYesOrNot(),
                                executorService
                        )
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