package org.maybe.rest;

import org.eclipse.microprofile.context.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.core.ResteasyContext;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.concurrent.*;

@Path("/v3-wa/maybe")
public class MaybeResourceV3WA
{
    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor executorService;

    @Inject
    @RestClient
    SomeoneElseMaybeResourceClient someoneElseClient;

    /* case 3.2.  Fix of 3.1 via wrapping of "allOf". ( why not all of CF static methods are in the ManagedExecutor? supplyAsync, runAsync, completedFuture but not allOf )*/
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> maybeYesOrNo()
    {
        CompletableFuture<String> chain1 = executorService.supplyAsync(() -> 42)
                .thenComposeAsync(
                        integer -> someoneElseClient.maybeYesOrNot().thenApplyAsync(v -> v, executorService),
                        executorService
                )
                .thenApplyAsync(
                        v -> v,
                        executorService
                );

        CompletableFuture<String> chain2 = threadContext.withContextCapture(CompletableFuture.allOf(chain1/*, chain2...*/))
                .thenApplyAsync(
                        aVoid -> chain1.join(),
                        executorService
                );

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