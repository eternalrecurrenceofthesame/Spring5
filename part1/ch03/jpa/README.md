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


