package nvpost.resto.dao;

import java.util.List;

import nvpost.resto.dao.mappers.RestaurantMapper;
import nvpost.resto.representations.Restaurant;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface RestaurantDAO {

    @Mapper(RestaurantMapper.class)
    @SqlQuery("select * from restaurant where name = :name")
    Restaurant getRestaurantByName(@Bind("name") String name);

    @Mapper(RestaurantMapper.class)
    @SqlQuery("select * from restaurant")
    List<Restaurant> getRestaurants();

    @SqlUpdate("insert into restaurant (name) values (:name)")
    void createRestaurant(@Bind("name") String name);

    @SqlUpdate("delete from restaurant where name = :name")
    void deleteRestaurant(@Bind("name") String name);

}
