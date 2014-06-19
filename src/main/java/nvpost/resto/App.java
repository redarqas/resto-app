package nvpost.resto;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nvpost.resto.dao.RestaurantDAO;
import nvpost.resto.resources.RestaurantResource;

import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resto application
 *
 */
public class App extends Application<AppConfig> {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<AppConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(AppConfig configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(AppConfig config, Environment env) throws Exception {
        // Database
        log.info("Initialize JDBI");
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(env, config.getDataSourceFactory(), "restodb");
        final RestaurantDAO restaurantDAO = jdbi.onDemand(RestaurantDAO.class);
        // Load resources routes
        log.info("Register Jersey");
        env.jersey().register(new RestaurantResource(restaurantDAO));
    }
}
