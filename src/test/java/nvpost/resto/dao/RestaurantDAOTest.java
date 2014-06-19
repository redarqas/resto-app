package nvpost.resto.dao;

import static org.fest.assertions.api.Assertions.assertThat;
import io.dropwizard.db.DataSourceFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import liquibase.Liquibase;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LockException;
import nvpost.resto.common.DBCommonTest;
import nvpost.resto.representations.Restaurant;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class RestaurantDAOTest extends DBCommonTest {
    private static final int quantity = 10;
    private static final String BASENAME = "Resto";
    private static RestaurantDAO dao;
    private static Liquibase liquibase;
    
    @BeforeClass
    public static void setUpBeforeClass() throws SQLException, LiquibaseException {
        final String url = "jdbc:h2:mem:./target/daoresto";
        final String user = "sa";
        final String pwd = "sa";
        final DataSourceFactory dsf = new DataSourceFactory();
        dsf.setDriverClass("org.h2.Driver");
        dsf.setUser(user);
        dsf.setPassword(pwd);
        dsf.setUrl(url);
        liquibase = initLiquibase(dsf);
        final DBI jdbi = new DBI(url, user, pwd);
        dao = jdbi.onDemand(RestaurantDAO.class);
    }
    
    @Before
    public void setUp() throws LiquibaseException {
        // Init db with migrations
        final String ctx = null;
        liquibase.update(ctx);
    }
    
    @Test
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
        final Restaurant random = findByName(founds);
        /**************************************
         * Delete restaurant
         **************************************/
        delete(random, founds);
    }
    
    private void delete(final Restaurant toDelete, final List<Restaurant> founds) {
        dao.deleteRestaurant(toDelete.getName());
        final List<Restaurant> remainings = dao.getRestaurants();
        final Set<Restaurant> diff = Sets.difference(new HashSet<Restaurant>(founds), new HashSet<Restaurant>(remainings));
        assertThat(diff).isNotEmpty();
        assertThat(diff.size()).isEqualTo(1);
        assertThat(Iterables.getFirst(diff, null)).isEqualTo(toDelete);
    }

    private Restaurant findByName(final List<Restaurant> founds) {
        final String randomName = String.format("%s%d", BASENAME, (new Random()).nextInt(quantity));
        final Restaurant found = dao.getRestaurantByName(randomName);
        assertThat(found).isNotNull();
        assertThat(found).isIn(founds);
        assertThat(found.getName()).isEqualTo(randomName);
        return found;
    }

    private List<Restaurant> list(final List<Restaurant> createdRestaurants) {
        final List<Restaurant> founds = dao.getRestaurants();
     // The result should not be empty
        assertThat(founds).isNotNull();
        assertThat(founds).isNotEmpty();
        // The result should contain the created elements
        assertThat(founds.size()).isEqualTo(createdRestaurants.size());
        assertThat(Sets.difference(new HashSet<Restaurant>(createdRestaurants), new HashSet<Restaurant>(founds))).isEmpty();
        return founds;
    }

    //Should not throw any exception
    private List<Restaurant> create() {
        final List<Restaurant> results = new ArrayList<Restaurant>();
        for (int i = 0; i < quantity; i++) {
            final String restoName = String.format("%s%d",BASENAME, i);
            dao.createRestaurant(restoName);
            results.add(new Restaurant(restoName));
        }
        return results;
    }

    @After
    public void tearDown() throws DatabaseException, LockException {
        liquibase.dropAll();
    }
}
