package org.maybe.rest;

import org.eclipse.microprofile.context.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.core.ResteasyContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.*;
import java.util.Optional;
import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.completedFuture;

@ApplicationScoped
public class MathOperationsManager
{
    @Inject
    ManagedExecutor executor;

    @Inject
    ThreadContext threadContext;

    @Inject
    @RestClient
    ExternalResourceClient externalClient;


    public CompletionStage<Response> doMathOperation1()
    {
        return threadContext.withContextCapture(
                //threadContext.withContextCapture(
                externalClient.getOne()
                        //)
                        .thenApplyAsync(one -> one * 3, executor)
                        .thenComposeAsync(
                                res -> completedFuture(checkResultAndReturn(res)),
                                executor
                        )
        );
    }

    public CompletionStage<Response> doMathOperation2()
    {
        CompletableFuture<Integer> f1 = threadContext.withContextCapture(externalClient.getOne()).toCompletableFuture();
        CompletableFuture<Integer> f2 = threadContext.withContextCapture(externalClient.getOne()).toCompletableFuture();
        return
                //threadContext.withContextCapture(
                CompletableFuture.allOf(f1, f2)
                        //)
                        .thenApplyAsync(ignored -> f1.join() + f2.join() + 1, executor)
                        .thenApplyAsync(
                                this::checkResultAndReturn,
                                executor
                        );
    }

    public CompletionStage<Response> doMathOperation3()
    {
        return threadContext.withContextCapture(
                //threadContext.withContextCapture(
                CompletableFuture.supplyAsync(() -> 3, executor)
                        //)
                        .thenComposeAsync(
                                multiplier -> externalClient.getOne().thenApplyAsync(one -> multiplier * one, executor),
                                executor
                        )
                        .thenComposeAsync(
                                res -> completedFuture(checkResultAndReturn(res)),
                                executor
                        )
        );
    }

    private Response checkResultAndReturn(Integer res)
    {
        return Optional
                .ofNullable(ResteasyContext.getContextData(HttpHeaders.class))
                .map(headers -> headers.getHeaderString("Authorization"))
                .map(
                        authToken -> Response.ok("final result is " + res).build()
                )
                .orElse(Response.status(401).entity("not authorized in main resource after call of external resource").build());
    }
}
