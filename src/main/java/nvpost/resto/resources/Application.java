package nvpost.resto.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class Application {
    @GET
    public Response getRestaurants() {
        URI uri = UriBuilder.fromUri("/restaurants").build();
        System.out.println("=============================>>>>>> Passege Application");
        return Response.ok().build();
    }
}
