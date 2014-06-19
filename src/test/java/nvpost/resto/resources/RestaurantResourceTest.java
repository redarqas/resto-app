package nvpost.resto.resources;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.dropwizard.testing.junit.ResourceTestRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.MediaType;

import nvpost.resto.dao.RestaurantDAO;
import nvpost.resto.representations.Restaurant;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;
import com.sun.jersey.api.client.GenericType;

public class RestaurantResourceTest {
    
    private static final RestaurantDAO dao = mock(RestaurantDAO.class);
    private  Restaurant uniqueResto;
    private  List<Restaurant> restaurants = new ArrayList<Restaurant>();

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder().addResource(new RestaurantResource(dao)).build();

    @Before
    public  void setUp() {
        // Setup fixture
        for (int i = 0; i < 10; i++) {
            restaurants.add(new Restaurant("Resto" + i));
        }
        uniqueResto = restaurants.get(0);
        // Setup mockito
        when(dao.getRestaurants()).thenReturn(restaurants);
        when(dao.getRestaurantByName(eq("Resto0"))).thenReturn(uniqueResto);
        
    }

    @Test
    @Ignore
    public void listRestaurants() {
        // Call restaurants list
        final List<Restaurant> results = resources.client().resource("/restaurants")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<List<Restaurant>>() {});
        // The result should not be empty
        assertThat(results).isNotNull();
        assertThat(results).isNotEmpty();
        // The result should contain the fixture elements
        assertThat(results.size()).isEqualTo(restaurants.size());
        assertThat(Sets.difference(new HashSet<Restaurant>(results), new HashSet<Restaurant>(results))).isEmpty();

    }

    @Test
    @Ignore
    public void randomRestaurant() {
        final Restaurant result = resources.client().resource("/restaurants/random").get(Restaurant.class);
        // The result should be one of restaurants
        assertThat(result).isIn(restaurants);
        // getRestaurants should be called once
        verify(dao, Mockito.times(1)).getRestaurants();
    }
    
    @Test
    @Ignore
    public void deleteRestaurant() {
        resources.client().resource("/restaurants/Resto0").delete();
        // getRestaurantByName/deleteRestaurant should be called once
        verify(dao, Mockito.times(1)).getRestaurantByName(Mockito.eq("Resto0"));
        verify(dao).deleteRestaurant(Mockito.eq("Resto0"));
    }

    @Test
    @Ignore
    public void createRestaurant() { 
        final String exceptedResto = "createdResto";
        final Restaurant result = resources.client().resource("/restaurants")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(Restaurant.class, new Restaurant(exceptedResto));
        //A result should be present and have
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(exceptedResto);
        //createRestaurant should be called once
        verify(dao, Mockito.times(1)).createRestaurant(exceptedResto);
    }

}
