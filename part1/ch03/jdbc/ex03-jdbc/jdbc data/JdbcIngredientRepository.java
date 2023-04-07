package tacos.data;


import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import tacos.Ingredient;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcIngredientRepository implements IngredientRepository{

    /**
     * NamedParameterJdbcTemplate
     * 순서대로 바인딩하지 않고 이름으로 바인딩하기
     */
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcIngredientRepository(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);

    }

    @Override
    public Iterable<Ingredient> findAll() {
        return jdbc.query("select id, name, type from Ingredient",
                this::mapRowToIngredient);
    }

    @Override
    public Optional<Ingredient> findById(String id) {

        String sql = "select id, name, type from Ingredient where id=:id";

        try{
            Map<String, Object> param = Map.of("id", id);
            Ingredient ingredient = jdbc.queryForObject(sql, param, this::mapRowToIngredient);
            return Optional.of(ingredient);
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        String sql = "insert into Ingredient (id, name, type) values (:id, :name, :type)";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", ingredient.getId())
                .addValue("name", ingredient.getName())
                .addValue("type", ingredient.getType().toString());

        jdbc.update(sql,param);

        return ingredient;
    }

    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
        return new Ingredient(
                rs.getString("id"),
                rs.getString("name"),
                Ingredient.Type.valueOf(rs.getString("type")));
    }

    /*
    private RowMapper<Ingredient> mapRowToIngredient(){
        return BeanPropertyRowMapper.newInstance(Ingredient.class);
    }
     */
}
