package org.maybe.rest;

import org.eclipse.microprofile.context.ThreadContext;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.concurrent.CompletionStage;

@Path("/main-service")
public class MainResource
{
    @Inject
    ThreadContext threadContext;

    @Inject
    MathOperationsManager mathOperationsManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<Response> call()
    {
        return threadContext.withContextCapture(mathOperationsManager.doMathOperation());
    }
}