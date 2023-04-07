# 데이터로 작업하기

## JDBC 를 사용해서 데이터 읽고 쓰기

#### + JdbcIngredientRepository 만들기

JdbcIngredientRepository 참고

```
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

#### ++ JdbcTacoRepository

JdbcTacoRepository 참고

우리가 만든 타코는 컬렉션으로 성분값을 가지고 있음 하지만 데이터베이스는 컬렉션을 저장할 수 없다!

그렇기 때문에 |Taco, TacoIngredient, Ingredient| 3 개의 테이블을 만들어서 관리하면 된다.

* 기본키 전략으로 Identity 를 사용할때 데이터베이스에 저장된 키값 가지고오는 방법 (JDBC)
```
KeyHolder keyHolder = new GeneratedKeyHolder();
jdbc.update(sql, param, keyHolder);

return keyHolder.getKey().longValue();

키홀더를 만들고 sql, 파라미터, 키홀더 값을 같이 넘겨주면 된다!
```

* DesignTacoController 
```
@SessionAttributes("order") ??

주문은 다수의 HTTP 요청에 걸쳐 존재해야 한다. 다수의 타코를 생성하고 하나의 주문으로
추가하기 위해 모델 정보를 http 세션값에 저장해서 응답
```

#### + JdbcOrderRepository

JdbcOrderRepository 참고

```
SimpleJdbcInsert 을 이용해서 insert sql 간소화 하기

private SimpleJdbcInsert orderInserter;

this.orderInserter = new SimpleJdbcInsert(dataSource)
                .withTableName("Taco_Order") // 테이블명
                .usingGeneratedKeyColumns("id"); // Identity 사용시 pk 칼럼
                //.usingColumns(" ... ") insert 칼럼을 지정하고 싶을 때 사용
```
```
SimpleJdbcInsert 를 사용할 때 파라미터 값을 매핑하는 방법

private ObjectMapper objectMapper;
this.objectMapper = new ObjectMapper();

Map<String, Object> values = objectMapper.convertValue(order, Map.class);
orderInserter.executeAndReturnKey(values).longValue();

오브젝트 매퍼를 만들고 객체를 맵 파라미터로 바꾼 다음 SimpleJdbcInsert 로 execute 하면 된다.

Map<String, Object> values = new HashMap();
values.put("tacoOrder", orderId);
values.put("taco", taco.getId);

orderTacoInserter.execute(values);

칼럼과 파라미터 값을 직접 넣고 싶다면 Map 객체를 만들고 SimpleJdbcInsert 를 사용하면 된다.
```

* OrderController



