# 데이터로 작업하기

## JDBC 를 사용해서 데이터 읽고 쓰기

* JdbcIngredientRepository 만들기
```
JdbcIngredientRepository 참고

@Autowired // 생성자가 하나면 안 해도 된다.
private final NamedParameterJdbcTemplate jdbc;

@Autowired
private final SimpleJdbcInsert jdbcInsert;

public JdbcIngredientRepository(DataSource dataSource) {
  this.jdbc = new NamedParameterJdbcTemplate(dataSource);

  this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                 .withTableName("Ingredient");  저장 테이블
                //.usingGeneratedKeyColumns("id") Auto 로 key 를 생성하는 칼럼 값을 지정한다.
                //.usingColumns(...)  xinsert 할 칼럼 생략 가능 특정 컬럼만 업데이트
    }

NamedParameterJdbcTemplate 를 이용해서 순서가 아닌 이름으로 값을 찾을 수 있다.
SimpleJdbcInsert 를 사용하면 쉽게 업데이트를 사용할 수 있다.
```
```
조회 메서드
String sql = "select id, name, type from Ingredient where id=:id";

Map<String, Object> param = Map.of("id", id);
jdbc.queryForObject(sql, param, this::mapRowToIngredient);

저장 메서드
SqlParameterSource param = new BeanPropertySqlParameterSource(ingredient);
jdbcInsert.execute(param);

Number key = jdbcInsert.executeAndReturnKey(param);
Auto increment 를 사용하면 저장후 키 값을 받아와서 키 값을 지정해주면 된다.

ingredient.setKey(key);
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

정의한 schema.sql 파일은 src/main/resources 폴더에 저장하자

