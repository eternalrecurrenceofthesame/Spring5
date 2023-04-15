# 리액티브 데이터 퍼시스턴스

블로킹이 없는 애플리케이션을 설계하려면 다른 컴포넌트들도 블로킹이 없어야 진정한 논 블로킹 

애플리케이션이 된다.

리포지토리에 값을 넣었는데 스레드 락이 걸리면 해당 리포지토리가 데이터를 생성하는 동안 애플리케이션은

블로킹 상태가 된다. 

컨트롤러 -> 데이터베이스에 이르기까지 데이터의 전체 플로우가 리액티브하고 블로킹되지 않아야 한다!

## 스프링 데이터의 리액티브 개념 이해하기

관계형 DB 나 JPA 는 리액티브 리포지토리가 지원되지 않는다. 그렇지만 관계형 DB 와 JPA 를 사용한다고 해서

리액티브를 사용할 수 없는 것은 아니다.

```
Mono 추출
tacoMono.lock();
tacoRepo.save(taco);

Flux 추출
tacoFlux.toIterable()
tacoRepo.saveAll(tacos);

tacoFlux.subscribe(taco -> {tacoRepository.save(taco));
subscribe 를 이용한 일괄처리 (리액티브 방식의 구독을 사용하므로 블로킹 일괄처리보다 바람직)

이처럼 리액티브를 블로킹한 상태에서 값을 저장하면됨. 이렇게 사용하는 경우 최소한의 경우에만 사용해야 한다.
```

## 리액티브 카산드라 리포지토리 사용하기

카산드라에 대한 기본적인 지식이 없기 때문에 일단 스킵(교재 내용도 한번에 안 읽어짐.)

## 리액티브 몽고 DB 리포지토리 작성하기

몽고DB 는 문서형 데이터베이스다. BSON 형식의 문서로 데이터를 저장하며 다른 데이터베이스에서

데이터를 쿼리하는 것과 거의 유사한 방법으로 문서를 쿼리하거나 검색할 수 있다.

```
* 의존성 추가

<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
<dependency>
			<groupId>de.flapdoodle.embed</groupId> // 내장 몽고 DB 사용
			<artifactId>de.flapdoodle.embed.mongo</artifactId>
</dependency>

실무에서 사용하는 몽고 DB 설정

data:
  mongodb:
    host: mongodb.tacocloud.com
    port: 27018 // 디폴트값 27018
    usernamd:
    password:
```

### 도메인 타입을 문서로 매핑하기

```
* Ingredient 참고 

@Document 를 사용해서 몽고디비에 저장될 문서라는 것을 나타낼 수 있다.
몽고디비 컬렉션(관계형데이터베이스의 테이블) 이름은 클래스와 같고 첫 자만 소문자다.

@Document(collection="ingredients") 를 이용해서 이름 설정을 변경할 수 있다.

@Id 는 springframework.data.annoation 를 사용한다.
아이디 속성은 String, Long, Serializable 을 사용할 수 있다. (자바에서 String 은 Serilizable 을 구현함)
```
```
* Taco 참고

몽고 DB 의 아이디 값으로 String 타입을 사용하면 이 속성의 값이 데이터베이스에 저장될 때 몽고 DB 가
자동으로 ID 값을 지정해준다(null 일 경우)

컬렉션을 저장할 때는 사용자 정의 타입? 을 만들 필요 없이 @Document 가 지정된 타입이나 단순 
자바 객체 타입이나 모두 저장할 수 있다. (따로 설정을 안해줘도 됨 ㄷㄷ) 431p
```
```
* Order 참고


```

