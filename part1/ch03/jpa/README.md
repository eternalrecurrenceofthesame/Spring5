# 스프링 데이터 JPA 를 사용해서 데이터 저장하고 사용하기

스프링 데이터는 여러 개의 하위 프로젝트들로 구성된다
```
스프링 데이터 JPA (RDB)
몽고DB, Neo4, 레디스, 카산드라
```

여기서는 SpringDataJpa 를 사용하겠음!

## JPA 를 이용해서 리포지토리 도메인 만들기
```
Ingredient, Order, Taco 에 JPA 애노테이션을 추가했다. 참고로 @ManyToMnay 를 사용했지만

실제 만들 때는 다대다 용 테이블을 만들어서 @ManyToOne, @OneToMnay 로 풀어가야 한다. (JPA 는 리포지토리 참고)
```
```
data.Repository 인터페이스는 CRUD Repository 를 상속받아서 스프링 데이터를 사용했다. 

인터페이스로 스프링 데이터를 상속 받으면 간단한 CRUD 는 따로 구현하지 않아도 되고 애플리케이션 실행 시점에

JPA 가 각 인터페이스의 구현체를 자동으로 생성해준다.
```

## CommandLineRunner 를 이용해서 데이터베이스에 값 저장하기

부트 스트랩 클래스에서 커맨드 라인 러너를 이용하면 데이터를 애플리케이션 실행 시점에 저장할 수 있다.

```
@Bean
public CommandLineRunner dataLoader(IngredientRepository repo){
return new CommandLineRunner(){
@Override
public void run(Sring... args) throws Exception{
 repo.save ... 데이터 저장 
}
};
} TacoCloudApplication 참고
```

## 기본적인 CURD 이외의 커스텀 쿼리 사용하기
```
* 특정 ZIP 코드로 배달된 모든 주문 데이터를 데이터베이스에서 가지고 와야 할 때
-> List<Order> findByDeliveryZip(String deliveryZip);

스프링 데이터는 주문 객체를 찾아야 한다는 것을 이미 알고 있다 상속 받을 때 데이터 
자료형과 키 자료형을 알려줬기 때문

* 지정된 일자 범위 내에서 특정 ZIP 코드로 배달된 모든 주문을 쿼리하기
-> List<Order> readOrdersByDeliveryZipAndPlacedAtBetween
     (String deliveryZip, Data startDate, Data endDate);
     
* 대소문자 무시하고 값 가지고 오기
List<Order> findByDeliveryToAndDeliveryCityAllIgnoresCase
(String deliveryTo, String deliveryCity);

* 지정된 열을 기준으로 정렬해서 값을 가지고 오기
List<Order> findByDeliveryCityOrderByDeliveryTo(String city);

메서드 쿼리만으로 표현하기 복잡하고 길어진다면 @Query 를 이용한 JPQL 을 사용하면 된다!
```

간략하게 JPA 를 이용해서 기존의 JDBC 템플릿을 대체했다. JPA 에 대한 자세한 내용은

JPA 리포지토리를 참고
