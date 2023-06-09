# 데이터로 작업하기

## JdbcIngredientRepository (NamedParameterJdbcTemplate 사용)

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
## JdbcTacoRepository (JDBC 를 사용해서 컬렉션 저장하기) 
```
* JdbcTacoRepository 참고

우리가 만든 타코는 컬렉션으로 성분값을 가지고 있다.(일대 다의 관계) 하지만 데이터베이스는 컬렉션을 저장할 수 없다! 그렇기 때문에 
테이블을 만들 때| Taco(타코), TacoIngredient(타코의 성분), Ingredient(성분) | 3 개의 테이블을 만들고 중간 테이블에서 

타코의 id 값과 성분 id 값을 가지는 TacoIngredient 테이블을 만들고 PK 값을 이용해서 관리하면 된다.
ex) 타코 1 (성분 1, 2) 
    타코 2 ( 성분 2, 3)
```
```
* 기본키 전략으로 Identity 를 사용할때 데이터베이스에 저장된 키값 가지고오는 방법 (JDBC)

KeyHolder keyHolder = new GeneratedKeyHolder();
jdbc.update(sql, param, keyHolder);

return keyHolder.getKey().longValue();

키홀더를 만들고 sql, 파라미터, 키홀더 값을 같이 넘겨주면 된다!
```

## JdbcOrderRepository (SimpleJdbcInsert 사용)
```
* JdbcOrderRepository 참고

SimpleJdbcInsert 을 이용해서 insert sql 간소화 하기

private SimpleJdbcInsert orderInserter;

this.orderInserter = new SimpleJdbcInsert(dataSource)
                .withTableName("Taco_Order") // 테이블명
                .usingGeneratedKeyColumns("id"); // Identity 사용시 pk 칼럼
                //.usingColumns(" ... ") insert 칼럼을 지정하고 싶을 때 사용
```
```
* SimpleJdbcInsert 를 사용할 때 파라미터 값을 매핑하는 방법 (주문 저장하기)

private ObjectMapper objectMapper;
this.objectMapper = new ObjectMapper();

private long saveOrderDetails(Order order){
        @SuppressWarnings("unchecked")
        Map<String, Object> values = objectMapper.convertValue(order, Map.class);
                                    values.put("placedAt", order.getPlacedAt()); 
                         // objectMapper 는 Date 값을 Long 으로 반환하므로 직접 매핑해야 한다. 97 p 

        long orderId = orderInserter
                .executeAndReturnKey(values).longValue();

return orderId;}

오브젝트 매퍼를 만들고 객체를 맵 파라미터로 바꾼 다음 SimpleJdbcInsert 로 execute 하면 된다.
```
```
* 컬렉션 저장하기

컬렉션은 앞서도 설명했지만 테이블에 직접 저장할 수 없기 때문에 아이디 값을 저장해야한다. 
칼럼과 파라미터 값을 직접 넣고 싶다면 Map 객체를 만들고 SimpleJdbcInsert 를 사용하면 된다.

private void saveTacoToOrder(Taco taco, long orderId){
       Map<String, Object> values = new HashMap<>();
       values.put("tacoOrder", orderId);
       values.put("taco", taco.getId());
       orderTacoInserter.execute(values);}
```

## Controller(@SessionAttribute 사용하기)

### DesignTacoController 참고
```
@SessionAttributes("order") 

주문은 다수의 HTTP 요청에 걸쳐 존재해야 한다. 다수의 타코를 생성하고 하나의 주문으로 추가하기 위해 모델 정보를 
http 세션값에 저장해서 응답한다. 

처음 DesignTacoController 에서 타코를 저장하고 주문 모델 값에 타코 정보를 저장한다.
그리고 주문 모델을 사용하는 OrderController 를 호출하면 기존의 주문 모델 세션값이 HTTP 요청에 저장되어 있기 때문에

주문 컨트롤러에서는 주문에 저장된 타코를 모델 값으로 사용할 수 있다. 
```
### OrderController
```
* OrderController 참고 

주문 컨트롤러에서는 타코 디자인 컨트롤러에서 세션값을 받고 주문을 데이터베이스에 저장한 후 세션을 완료해서 이전 세션값을 초기화한다.
```
```
IngredientByIdConverter

JdbcIngredientRepository 에서 아이디 값으로 Ingredient 객체를 찾아서 반환하는 컨버터 클래스 IngredientByIdConverter 참고
```

#### ++ 내장 H2 데이터베이스를 인터넷 클라이언트에서 사용해보기
```
DevTools + H2 데이터베이스를 사용하면 웹 브라우저에서 사용할 수 있는 H2 콘솔을 제공해준다 28p

localhost:8080/h2-console 
JDBC URL: jdbc:h2:mem:testdb
user: sa
```

## th:each 를 사용해서 타코 이름 보여주기 

orderForm.html

```
<ul>
  <li th:each="taco : ${order.tacos}">
    <span th:text="${taco.name">taco name</span>
  </li>
</ul> 

each 로 값을 하나씩 빼와서 주문 한 타코 이름을 보여준다! 
```
