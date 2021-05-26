package org.maybe.rest;

import org.jboss.resteasy.core.ResteasyContext;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Optional;

@ApplicationScoped
@Path("/external")
public class ExternalResource
{
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/one")
    public Response getOne()
    {
        return Optional
                .ofNullable(ResteasyContext.getContextData(HttpHeaders.class))
                .map(headers -> headers.getHeaderString("Authorization"))
                .map(
                        authToken -> Response.ok(1).build()
                )
                .orElse(Response.status(401).entity( "not authorized in external resource").build());
    }
}