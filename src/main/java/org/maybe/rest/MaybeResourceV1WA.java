package org.maybe.rest;

import org.eclipse.microprofile.context.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.core.ResteasyContext;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.concurrent.*;

@Path("/v1-wa/maybe")
public class MaybeResourceV1WA
{
    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor executorService;

    @Inject
    @RestClient
    SomeoneElseMaybeResourceClient someoneElseClient;

    /* case 1.3. Fix of 1.2 via wrapping of the client's response */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> maybeYesOrNo()
    {
        return threadContext.withContextCapture(
                threadContext.withContextCapture(someoneElseClient.maybeYesOrNot()).thenApplyAsync(v -> v, executorService)
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

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public CompletionStage<String> maybeYesOrNo()
//    {
//        return threadContext.withContextCapture(
//                CompletableFuture.supplyAsync(() -> 42, executorService)
//                        .thenComposeAsync(
//                                integer -> someoneElseClient.maybeYesOrNot().thenApplyAsync(v -> v, executorService),
//                                executorService
//                        )
//                        .thenComposeAsync(answerFromSomeone -> {
//                            if (answerFromSomeone.equals("no"))
//                            {
//                                return CompletableFuture.completedFuture("no");
//                            }
//
//                            List<String> authHeader = ResteasyContext.getContextData(HttpHeaders.class).getRequestHeader("Authorization");
//                            if (authHeader.isEmpty())
//                            {
//                                return CompletableFuture.completedFuture("no");
//                            }
//                            else
//                            {
//                                return CompletableFuture.completedFuture("yes");
//                            }
//                        }, executorService)
//        );
//    }