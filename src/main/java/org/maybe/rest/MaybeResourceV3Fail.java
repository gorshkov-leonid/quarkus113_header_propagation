package org.maybe.rest;

import org.eclipse.microprofile.context.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.core.ResteasyContext;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.concurrent.*;

@Path("/v3-fail/maybe")
public class MaybeResourceV3Fail
{
    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor executorService;

    @Inject
    @RestClient
    SomeoneElseMaybeResourceClient someoneElseClient;

    /* case 3.1. The same as WA 2.4 but is broken with help "allOf" */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> maybeYesOrNo()
    {
        CompletableFuture<String> chain1 = executorService.supplyAsync(() -> 42)
                .thenComposeAsync(
                        integer -> someoneElseClient.maybeYesOrNot().thenApplyAsync(v -> v, executorService).toCompletableFuture(),
                        executorService
                );

        CompletableFuture<String> chain2 = CompletableFuture.allOf(chain1).thenApplyAsync(aVoid -> chain1.join(), executorService);

        return threadContext.withContextCapture(
                chain2
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