package org.maybe.rest;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.*;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.*;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.*;

@OpenAPIDefinition(
        info = @Info(title = "Maybe App", version = "unknown"),
        security = @SecurityRequirement(name = "apiKey")
)
@SecuritySchemes({
        @SecurityScheme(type = SecuritySchemeType.APIKEY, apiKeyName = HttpHeaders.AUTHORIZATION, in = SecuritySchemeIn.HEADER, securitySchemeName = "apiKey")
})
@ApplicationPath("/")
public class MaybeApplication extends Application
{
}
