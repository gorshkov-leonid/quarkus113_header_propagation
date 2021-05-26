
package org.maybe.rest;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@Path("/external")
@RegisterRestClient
@RegisterClientHeaders
public interface ExternalResourceClient
{
    @GET
    @Path("/one")
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<Integer> getOne();
}