package nvpost.resto.common;

import io.dropwizard.db.DataSourceFactory;

import java.sql.SQLException;
import java.util.Properties;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class DBCommonTest {

    protected static Liquibase initLiquibase(final DataSourceFactory dataSourceFactory) throws SQLException, LiquibaseException {
        final Properties info = new Properties();
        info.setProperty("user", dataSourceFactory.getUser());
        info.setProperty("password", dataSourceFactory.getPassword());
        final org.h2.jdbc.JdbcConnection h2Conn = new org.h2.jdbc.JdbcConnection(dataSourceFactory.getUrl(), info);
        final JdbcConnection conn = new JdbcConnection(h2Conn);
        final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(conn);
        return new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), database);
    }

}