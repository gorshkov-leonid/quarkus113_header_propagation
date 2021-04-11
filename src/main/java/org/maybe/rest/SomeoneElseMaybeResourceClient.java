
package org.maybe.rest;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

//todo !!!!!!!!!!!!!
@ApplicationScoped
@Path("/someone")
@RegisterRestClient
@RegisterClientHeaders
public interface SomeoneElseMaybeResourceClient
{
    @GET
    @Path("/maybe")
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<String> maybeYesOrNot();
}