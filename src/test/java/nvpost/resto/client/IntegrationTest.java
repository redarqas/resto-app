package nvpost.resto.client;

import static org.fest.assertions.api.Assertions.assertThat;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import liquibase.Liquibase;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LockException;
import nvpost.resto.App;
import nvpost.resto.AppConfig;
import nvpost.resto.common.DBCommonTest;
import nvpost.resto.representations.Restaurant;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

public class IntegrationTest extends DBCommonTest {
    private static final String PORT = "8090";
    private static final String SERVER_URL = String.format("http://localhost:%s", PORT);
    private static Client client;
    private static Liquibase liquibase;

    @ClassRule
    public static final DropwizardAppRule<AppConfig> RULE = new DropwizardAppRule<AppConfig>(App.class, "conf/test.yaml");

    @BeforeClass
    public static void setUpBeforeClass() throws SQLException, LiquibaseException {
        client = new Client();
        liquibase = initLiquibase(RULE.getConfiguration().getDataSourceFactory());
    }

    @Before
    public void setUp() throws LiquibaseException {
        // Init db with migrations
        final String ctx = null;
        liquibase.update(ctx);
    }

    @Test
    // @Ignore
    public void interaction() {
        /**************************************
         * Create restaurant
         **************************************/
        final List<Restaurant> createdRestaurants = create();
        /**************************************
         * List restaurants
         **************************************/
        final List<Restaurant> founds = list(createdRestaurants);
        /**************************************
         * Random restaurant
         **************************************/
        final Restaurant random = random(founds);
        /**************************************
         * Delete restaurant
         **************************************/
        delete(random, founds);
    }

    private List<Restaurant> create() {
        final String expectedName = "createdResto"; 
        final URI createURI = URI.create(String.format("%s/%s", SERVER_URL, "restaurants")); 
        final List<Restaurant> results = new ArrayList<Restaurant>();
        // Create a restaurants
        final ClientResponse createResponse = client.resource(createURI).type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, new Restaurant(expectedName));
        // Http code should be 201
        assertThat(createResponse.getStatus()).isEqualTo(201);
        // Location should be assigned with the restaurant name
        String newURL = createResponse.getHeaders().get("Location").get(0);
        assertThat(newURL).isEqualTo(String.format("%s/%s/%s", SERVER_URL, "restaurants", expectedName) );
        final Restaurant uniqueRestaurant = createResponse.getEntity(Restaurant.class);
        assertThat(uniqueRestaurant).isNotNull();
        assertThat(uniqueRestaurant.getName()).isEqualTo(expectedName);
        results.add(uniqueRestaurant);
        return results;
    }

    private List<Restaurant> list(final List<Restaurant> createdRestaurants) {
        final URI collectionURI = URI.create(String.format("%s/%s", SERVER_URL, "restaurants"));
        final ClientResponse listResponse = client.resource(collectionURI).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        assertThat(listResponse.getStatus()).isEqualTo(200);
        final List<Restaurant> founds = listResponse.getEntity(new GenericType<List<Restaurant>>() {});
        // The result should not be empty
        assertThat(founds).isNotNull();
        assertThat(founds).isNotEmpty();
        // The result should contain the created elements
        assertThat(founds.size()).isEqualTo(createdRestaurants.size());
        assertThat(Sets.difference(new HashSet<Restaurant>(createdRestaurants), new HashSet<Restaurant>(founds))).isEmpty();
        return founds;
    }

    private Restaurant random(final List<Restaurant> founds) {
        final URI randomURI = URI.create(String.format("%s/%s/%s", SERVER_URL, "restaurants", "random"));
        final ClientResponse response = client.resource(randomURI).get(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(200);
        final Restaurant result = response.getEntity(Restaurant.class);
        // The result should be one of restaurants
        assertThat(result).isNotNull();
        assertThat(result).isIn(founds);
        return result;
    }

    private void delete(final Restaurant toDelete, final List<Restaurant> founds) {
        final URI collectionURI = URI.create(String.format("%s/%s", SERVER_URL, "restaurants"));
        final URI deleteURI = URI.create(String.format("%s/%s/%s", SERVER_URL, "restaurants", toDelete.getName()));
        final ClientResponse response = client.resource(deleteURI).delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(204);
        final List<Restaurant> remainings = client.resource(collectionURI).accept(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<List<Restaurant>>() {
                });
        final Set<Restaurant> diff = Sets.difference(new HashSet<Restaurant>(founds), new HashSet<Restaurant>(remainings));
        assertThat(diff).isNotEmpty();
        assertThat(diff.size()).isEqualTo(1);
        assertThat(Iterables.getFirst(diff, null)).isEqualTo(toDelete);
    }

    @After
    public void tearDown() throws DatabaseException, LockException {
        liquibase.dropAll();
    }

}
