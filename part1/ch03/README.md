# 데이터로 작업하기

## JDBC 를 사용해서 데이터 읽고 쓰기

* JdbcIngredientRepository
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
