# 리액터 개요 
```
* 애플리케이션 코드의 형태

명령형: 한줄씩 순차적으로 데이터를 모아서 처리
리액티브: 작업을 병렬적으로 실행 ex) 신문 정기 구독 
```
## 리액티브 프로그래밍이란?
```
리액티브 프로그래밍은 작업 단계를 순차적으로 기술(명령형)하는 것이 아니라 데이터가 전달될 파이프라인을 
구성해야한다. 그리고 파이프라인을 통해 데이터가 전달되는 동안 데이터는 어떤 형태로든 변경 사용 될 수 있다.
```
```
* 명령형 vs 리액티브

String name = "Craig";
String captialName = name.toUpperCase();
String greeting = "Hello, " + capitalName + "!";
System.out.println(greeting); 

각 단계가 완료될 때까지 실행중인 스레드는 다음 단계로 이동하지 못한다.

Mono.just("Craig").map(n -> n.toUpperCase()).map(cn -> "Hello, " + cn + "!")
                   .subscribe(System.out::println);

스트림(파이프라인?) 에 세 개의 모노가 만들어진다. (just, map, map)
처음 모노 데이터가 생성되고 방출, map 에서 데이터 변경 후 모노를 생성 방출되는 메커니즘이다

.subscrie 으로 구독하고 구독자가 생성되면 데이터를 넘겨준다.

각 오퍼레이션 단계는 같은 스레드로 실행되거나 다른 스레드로 실행될 수 있다.
리액티브는 병렬적으로 작업이 진행되므로 Mono 를 호출하고 스레드는 다른 작업을 수행할 수 있다.


Mono: 하나의 데이터 항목만 갖는 파이프라인 // Flux 0,1 또는 다수의 데이터를 갖는 파이프라인 346p
```

### 리액티브 타입(Flux, Mono) 생성하기

* FluxCreationTests 참고 

```
* 객체로부터 생성하기

Flux<String> fruitFlux = 
             Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry"); (발행자)
Flux.just 로 데이터 생성

fruitFlux(구독자).subscribe(f -> System.out.println("Here's some fruit: " + f));

구독자 추가 메서드인 subscribe 를 호출해서 subscriber 를 만듦, subscribe 호출 즉시
데이터가 전송된다. Data ---- f 349p

Publisher 인터페이스에는 Subscriber 가 Publisher 를 구독 신청할 수 있는 subscribe() 메서드
한 개가 선언되어 있다.

StepVerifier.create(fruitFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .expectNext("Grape")
                .expectNext("Banana")
                .expectNext("Strawberry")
                .verifyComplete();

StepVerifier 는 해당 리액티브 타입을 구독한 후 스트림을 통해 전달되는 데이터에 assertion 을 적용,
해당 스트림이 기대한 대로 작동하는지 검사.
```
```
* 컬렉션으로부터 생성하기

createAFlux_fromArray, createAFlux_fromIterable, createAFlux_fromStream 참고
```
```
* Flux 데이터 생성하기

데이터 없이 매번 새 값으로 증가하는 숫자를 방출하는 카운터 역할의 Flux 만들어보기
(데이터 없는 방출 flux) 

Flux.range(1,5);  // start:1, count:5

Flux.interval(Duration.ofSeconds(1)).take(5); 
// 1 초 간격으로 5 번 생성, interval 은 0 부터 시작한다. take 해주지 않으면 계속 생성한다.

createAFlux_range, createAFlux_interval 참고 
```

### 리액티브 타입 조합하기

두 개의 리액티브 타입을 결합하거나 하나의 Flux 를 두 개 이상의 리액티브로 분할해야 하는 경우가 

있다. Flux 나 Mono 를 결합하거나 분리해보기

```
* 리액티브 타입 결합 

두 개의 Flux 스트림(방출 값)을 하나의 결과 Flux 로 생성하기 

    Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofMillis(500)); // 500 밀리초마다 방출
    Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")
                .delayElements(Duration.ofMillis(250))
                .delayElements(Duration.ofMillis(500)); // 750 밀리초마다 방출 

characterFlux.mergeWith(foodFlux); 
// 플럭스 생성 시간 딜레이를 설정해서 merge 방출 순서를 조절 할 수 있지만 flux 값이 완벽하게 
번갈아 방출되는 것을 보장하지 않는다.

번갈아 방출되는 것을 보장하려면 zip() 오퍼레이션을 사용하면 된다.
```
```
* zip 사용 

public void zipFluxes() 참고 
Flux<Tuple2<String, String>> zippedFlux = Flux.zip(characterFlux, foodFlux);
```
```
* 먼저 값을 방출하는 리액티브 타입 선택하기

두 개의 Flux 객체를 결합하는대신 먼저 방출하는 소스 Flux 값을 발행하는 Flux 생성하기
slowFlux 에 .delaySubscription 을 줘서 fastFlux 를 먼저 구독하고 발행한다. 

first 는 deprecate 되었지만 예제 진행을 위해 사용.. public void firstFlux() 참고

```

### 리액티브 스트림의 변환과 필터링

스트림 동안 다른 값으로 변경하거나 걸러내기

```
* 리액티브 타입으로부터 데이터 필터링하기 

Flux<String> skipFlux = Flux.just("one","two","skip a few", "ninety nine", "one hundred")
                .skip(3);
앞 세 개를 스킵하고 99, 100 만 생성한다, take 를 사용하면 지정된 수 또는 지정된 시간 동안만 방출한다.

skipAFewSeconds, take, take2 참고
```
```
* .filter 오퍼레이션을 이용한 조건식 만들기 

filter 를 이용해서 조건식을 만들 수 있고, distint 를 사용해서 중복을 거를 수 있다.
filter,distinct 참고
```

### 리액티브 데이터 매핑하기
```
* map 과 flatMap 사용하기

map 을 사용해서 매핑하면 각 항목이 동기적으로 발행된다 map 테스트 참고 

flatMap 은 각항목을 비동기적(병행 처리)으로 매핑을 수행한다.

flatMap 을 사용하면 새로 발행한 값을 Mono 나 Flux 로 매핑한 후 결과값을 새로운 Flux 로 만들어준다.
flatMap 과 subscribeOn 을 함께 사용하면 리액터 타입의 변환을 비동기적으로 수행할 수 있다.

Flux<Player> playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
          .flatMap(n -> Mono.just(n)
                 .map(p -> {
                       String[] split = p.split("\\s");
                       return new Player(split[0], split[1]);
                    }).subscribeOn(Schedulers.parallel())
                );

위 로직에서 flatMap 으로 모노를 만들고 map 으로 매핑까지만 하면 일반 map 을 사용하는 것과 마찬가지로
순서로(동기) 로 오퍼레이션을 수행한다.

그러나 마지막에 subscribeOn() 을 호출하면 각 구독이 병렬 스레드로 수행되어야 한다는 것을 나타낸다.

flatMap() 과 subscribeOn() 을 사용해서 비동기로 처리하면 다수의 병렬 스레드로 작업을 분할해서
스트림의 처리량을 증가시킬 수 있다는 것이다.

그러나 병렬로 작업이 수행되기 때문에 어떤 작업이 먼저 끝날지 보장되지 않는다.
```








