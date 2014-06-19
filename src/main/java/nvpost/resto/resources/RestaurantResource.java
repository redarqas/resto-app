package nvpost.resto.resources;

import java.net.URI;
import java.util.List;
import java.util.Random;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nvpost.resto.dao.RestaurantDAO;
import nvpost.resto.representations.Restaurant;

import org.hibernate.validator.constraints.NotBlank;

@Path("/restaurants")
@Produces(MediaType.APPLICATION_JSON)
public class RestaurantResource {

    private final RestaurantDAO dao;

    public RestaurantResource(RestaurantDAO dao) {
        this.dao = dao;
        
    }

    @GET
    public Response getRestaurants() {
        final List<Restaurant> restaurants = dao.getRestaurants();
        return Response.ok(restaurants).build();
    }

    @GET
    @Path("/random")
    public Response getRandomResto() {
        final List<Restaurant> restaurants = dao.getRestaurants();
        if (restaurants != null && !restaurants.isEmpty()) {
            final Restaurant randomResto = restaurants.get(new Random().nextInt(restaurants.size()));
            return Response.ok(randomResto).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRestaurant(@Valid Restaurant restaurant) {
        dao.createRestaurant(restaurant.getName());
        final URI createdURI = URI.create(String.format("%1$s", restaurant.getName()));
        return Response.created(createdURI).entity(restaurant).build();
    }

    @DELETE
    @Path("/{name}")
    public Response deleteRestaurant(@PathParam("name") @NotBlank String name) {
        final Restaurant restaurant = dao.getRestaurantByName(name);
        if (restaurant != null) {
            dao.deleteRestaurant(name);
            return Response.noContent().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

}
