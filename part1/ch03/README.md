# 데이터로 작업하기

## JDBC 를 사용해서 데이터 읽고 쓰기

* JdbcIngredientRepository 만들기
```
JdbcIngredientRepository 참고

private final NamedParameterJdbcTemplate jdbc;

public JdbcIngredientRepository(DataSource dataSource) {
  this.jdbc = new NamedParameterJdbcTemplate(dataSource);
}

NamedParameterJdbcTemplate 를 이용해서 순서가 아닌 이름으로 조회,수정,저장 
```
```
이름으로 sql 파라미터 값 저장하기

* Map 사용
String sql = "select id, name, type from Ingredient where id=:id";
Map<String, Object> param = Map.of("id", id);

jdbc.queryForObject(sql, param, this::mapRowToIngredient);

* MapSqlParameterSource 사용
String sql = "insert into Ingredient (id, name, type) values (:id, :name, :type)";

SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", ingredient.getId())
                .addValue("name", ingredient.getName())
                .addValue("type", ingredient.getType().toString());

jdbc.update(sql,param);
```
```
매퍼 만들기 (수작업)
 private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
      return new Ingredient(
           rs.getString("id"),
           rs.getString("name"),
           Ingredient.Type.valueOf(rs.getString("type")));
    }

매퍼 만들기 (간소)
private RowMapper<Ingredient> mapRowToIngredient(){
 return BeanPropertyRowMapper.newInstance(Ingredient.class);
}

간소화 매퍼를 만들 때 데이터베이스 컬럼 명과 클래스 필드값이 다름 자바는 카멜로테이션을 사용하지만 
데이터베이스는 _ 표기로 구분함

itemName / item_name 이런 경우 매핑시 카멜로테이션 규칙을 이용해서 set 메서드를 만들어줌
Item item = new Item();
item.setItemName(rs.getString("item_name"));

문제는 데이터베이스 칼럼명과 클래스 필드값이 위 규칙으로 적용되지 않는 경우임
username / member_name 이런 경우 값을 찾아올 때 별칭을 주면 됨

select member_name as username 그러면 값을 조회할 때 카멜로테이션이 정상 적용된다.
member.setUsername(rs.getString("username")); 

TIP 간소화 버전은 호출할 때 메서드 명으로 사용해야 한다
ex)Ingredient ingredient = jdbc.queryForObject(sql, param, mapRowToIngredient());
```

* 컨트롤러에 리포지토리 주입해서 사용하기

DesignTacoController 참고

* 스키마 정의 및 데이터 추가

정의한 schema.sql 파일은 src/main/resources 폴더에 저장하자 data.sql, schema.sql 참고



