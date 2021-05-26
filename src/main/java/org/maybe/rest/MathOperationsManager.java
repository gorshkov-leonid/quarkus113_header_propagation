package org.maybe.rest;

import org.eclipse.microprofile.context.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.core.ResteasyContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.*;
import java.util.Optional;
import java.util.concurrent.*;

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

    public CompletionStage<Response> doMathOperation()
    {
        CompletableFuture<Integer> f1 = getOne().toCompletableFuture();
        CompletableFuture<Integer> f2 = getOne().toCompletableFuture();
        return CompletableFuture.allOf(f1, f2)
                .thenComposeAsync(
                        ignored -> {
                            Integer v1 = f1.join();
                            Integer v2 = f1.join();
                            return getOne()
                                    .thenApplyAsync(
                                            v3 -> {
                                                return v1 + v2 + v3;
                                            },
                                            executor
                                    );
                        },
                        executor
                )
                .thenApplyAsync(
                        res ->
                                Optional
                                        .ofNullable(ResteasyContext.getContextData(HttpHeaders.class))
                                        .map(headers -> headers.getHeaderString("Authorization"))
                                        .map(
                                                authToken -> Response.ok("final result is " + res).build()
                                        )
                                        .orElse(Response.status(401).entity("not authorized in main resource after call of external resource").build()),
                        executor
                );
    }

    private CompletionStage<Integer> getOne()
    {
        return threadContext.withContextCapture(externalClient.getOne());
    }
}
