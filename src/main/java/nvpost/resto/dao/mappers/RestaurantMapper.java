package nvpost.resto.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import nvpost.resto.representations.Restaurant;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class RestaurantMapper implements ResultSetMapper<Restaurant> {
    //Construct Restaurant instance from SQL ResultSet
    public Restaurant map(int index, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new Restaurant(resultSet.getString("name"));
    }

}
