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
    @Path("/v1")
    public CompletionStage<Response> op1()
    {
        return threadContext.withContextCapture(mathOperationsManager.doMathOperation1());
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/v2")
    public CompletionStage<Response> op2()
    {
        return threadContext.withContextCapture(mathOperationsManager.doMathOperation2());
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/v3")
    public CompletionStage<Response> op3()
    {
        return threadContext.withContextCapture(mathOperationsManager.doMathOperation3());
    }
}