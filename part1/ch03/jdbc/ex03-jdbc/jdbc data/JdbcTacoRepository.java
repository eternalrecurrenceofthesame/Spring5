package tacos.data;

import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tacos.Ingredient;
import tacos.Taco;

import javax.sql.DataSource;
import java.time.LocalDate;

@Repository
public class JdbcTacoRepository implements TacoRepository{

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcTacoRepository(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * 타코 필드와 성분 컬렉션 필드를 저장하는 메서드들
     *
     * 타코에 있는 성분 컬렉션을 저장하기 위해서 타코 pk + 성분 pk 값을 컬럼으로 가지는
     * 중간 테이블(Taco_Ingredients)을 만들어서 관리
     *
     * 타코, 타코 성분, 성분 이렇게 총 3 개의 테이블로 관리하는 구조가 된다.
     */
    @Override
    public Taco save(Taco taco) {
        long tacoId = saveTacoInfo(taco);
        taco.setId(tacoId);

        for(Ingredient ingredient : taco.getIngredients()){
            saveIngredientTaco(ingredient, tacoId);
        }

        return taco;
    }

    private long saveTacoInfo(Taco taco){
        taco.setCreatedAt(LocalDate.now()); // 타코 생성 시간
        String sql = "insert into Taco (name, createdAt) values (:name, :createdAt)";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("name", taco.getName())
                .addValue("createdAt", taco.getCreatedAt());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql,param,keyHolder);

        return keyHolder.getKey().longValue();
    }

    private void saveIngredientTaco(Ingredient ingredient, long tacoId){
        String sql = "insert into Taco_Ingredients(taco, ingredient) values(:taco, :ingredient)";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("taco", tacoId)
                .addValue("ingredient", ingredient.getId());

        jdbc.update(sql, param);
    }



}
