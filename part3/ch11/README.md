# 리액티브 API 개발하기

## 스프링 WebFlux 사용하기

스프링 MVC 같은 서블릿 기반의 웹 프레임워크는 스레드 풀에서 작업 스레드를 가져와서 해당 요청을 처리하고

작업이 종료될 때까지 요청 스레드가 블로킹된다.

오늘날에는 다양한 사물인터넷(클라이언트) 에서 웹 API 를 요청하고 데이터를 교환한다. 웹 애플리케이션을 

사용하는 클라이언트 수가 증가함에 따라 웹 어플리케이션의 확장성이 중요해졌다!

비동기 웹 프레임워크는 더 적은 스레드로 (CPU 코어당 하나의 스레드) 더 높은 확장성을 가져갈 수 있다.

이벤트루핑 기법을 이용해서 하나의 스레드가 더 많은 요청을 처리할 수 있다.

### 스프링 WebFlux 개요
```
MVC 와 WebFlux 는 같은 애노테이션을 공유한다.
웹 플럭스는 내장 서버로 톰캣 대신 비동기 이벤트 중심 서버인 Netty 를 사용한다.

스프링 MVC 도 리액티브 타입을 반환할 수 있다 단지 MVC 는 다중 스레드에 의존하는
서블릿 기반 웹 프레임워크라면 

WebFlux 는 요청이 이벤트 루프로 처리되는 진정한 리액티브 웹 프레임워크이다.
```

### 리액티브 컨트롤러 작성하기

DesignTacoController 참고

```
리액티브 컨트롤러는 리액티브 엔드-to-엔드 스택의 제일 끝에 위치하게 된다.

클라이언트 -> 컨트롤러 -> 서비스(선택적) -> 리포지토리
        리포지토리 -> 서비스(선택적) -> 컨트롤러(리액티브 타입 반환)
        
리액티브 컨트롤러는 MVC 컨트롤러와 크게 다르지 않고 반환 타입만 다르다.

Flux<Taco> 와 같은 리액티브 타입을 받을 때는 구독자(클라이언트) 추가 메서드 .subscribe() 를 
호출할 필요가 없다. 프레임워크가 대신 호출해준다! 

단일 값을 반환할 때는 Mono 타입을 사용하면된다!
```
```
* RxJava 타입 사용하기

웹 플럭스를 사용할 때는 Flux,Mono 같은 리액티브 타입이 자연스러운 선택 이지만,
Observable 이나 Single 같은 RxJava 타입을 사용할 수도 있다. 380p
```
```
* 리액티브하게 입력 처리하기 postTaco 메서드 참고 

MVC RestController 를 사용해서 데이터를 인자로 받으면 @RequestBody 애노테이션을 이용해서 
객체로 만들어야 한다. 그리고 데이터를 사용할 서비스나 리포지토리를 호출한다.

이렇게되면 총 두 번의 스레드 블로킹이 발생한다 처음 인자를 받을 때, 애플리케이션 로직을 호출할 때 

리액티브하게 입력처리를 하고싶다면 Publisher 인터페이스를 구현한 타입(Mono, Flux) 을 인자로 받는다
그러면 요청 메서드 몸체의 서비스나 리포지토리는 Taco 객체가 분석되는 것을 기다리지 않고 즉시 호출된다.

리포지토리도 리액티브로 만들었으므로 Mono 타입을 받고 즉시 Flux 로 반환된다. 마지막으로 next 를 호출해서
Flux 를 Mono<Taco> 값으로 반환하도록 하면 Mono 타입으로 값을 반환할 수 있다.

Flux: 0, 1 또는 다수의 데이터를 갖는 파이프라인을 가진다
Mono: 하나의 데이터 항목만 갖는 리액티브 타입
```
### 함수형으로 API 요청 핸들러 만들어보기! (간단하게 API 등록)

```
* 스프링의 함수형 플로그래밍 API 작성에 사용되는 타입들

RequestPredicate: 처리될 요청의 종류를 선언한다.
RouterFunction: 라우팅된 요청이 어떻게 핸들러에 전달되어야 하는지 선언한다.
ServerRequest: HTTP 요청을 나타내며, 헤더와 몸체 정보를 사용할 수 있다.
ServerResponse: HTTP 응답을 나타내며, 헤더와 몸체 정보를 포함한다.
```
```
* 함수형 API 예시 demo.RouterFunctionConfig 참고 

 @Bean
public RouterFunction<?> helloRouterFunction(){
        return RouterFunctions.route(RequestPredicates.GET("/hello"),
                ServerRequest -> ServerResponse.ok().body(just("Hello World!"),String.class))
                .andRoute(RequestPredicates.GET("/bye"),
                ServerRequest -> ServerResponse.ok().body(just("See Ya!"), String.class));
    }

다른 종류의 요청도 처리해야하는 경우 .andRoute 로 간단하게 라우팅할 수 있다.

 @Bean
public RouterFunction<?> routerFunction() {
     return RouterFunctions.route(RequestPredicates.GET("/design/taco"), this::recents)
                .andRoute(RequestPredicates.POST("/design"), this::postTaco);
    }
      
Get 으로 최근 타코를 호출하는 라우터와 Post 로 타코를 저장하는 라우터
호출 로직들은 RouterFunctionConfig 참고 
```

### 리액티브 컨트롤러 테스트하기
```
스프링 5 가 제공하는 WebTestClient 를 사용하면 WebFlux 를 사용하는 리액티브 컨트롤러의
테스트를 쉽게 작성할 수 있다.
```
```
* DesignTacoControllerTest 참고

shouldReturnRecentTacos
WebTestClient testClient = WebTestClient.bindToController(new DesignTacoController(tacoRepository))
                .build(); // TestClient 생성

testClient.get().uri("/design/recent")
                .exchange() // 최근 타코 요청 (API 요청을 제출한다)
                .expectStatus().isOk() // 기대한 응답인지 검사
                .expectBody()
                .jsonPath("$").isArray() // $ 를 사용하면 루트 검사를할 수 있다
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(tacos[0].getId().toString()) //Json 패스를 이용한 값 꺼내기
                .jsonPath("$[0].name").isEqualTo("Taco 1") 
```
```
* A Taco 저장 테스트하기

shouldSaveATaco 메서드 참고

 testClient.post().uri("/design")
                .contentType(MediaType.APPLICATION_JSON)
                .body(tacoMono, Taco.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Taco.class) // 페이로드 값과 저장된 타코 객체가 같은 타입인지 체크
                .isEqualTo(taco);

논블로킹 /design 메서드는 저장이 안 됨.. 블로킹 메서드로 대체해서 테스트 했음. 
DesignTacoController 참고
```
```
* 실행중인 서버로 테스트하기(통합 테스트)

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DesignTacoControllerTest {
    
    @Autowired
    private WebTestClient testClient;

Netty 서버로 통합 테스트하기 ! 사실 말이 서버를 띄우고 하는 통합테스트지
앞에서 한 Mock 테스트랑 같다.

testClient.get().uri("/design/recent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[?(@.id == 'TACO1')].name")
                .isEqualTo("Carnivore")
                .jsonPath("$[?(@.id == 'TAC02')].name")
                .isEqualTo("Bovine Bounty");
```
```
* 간단히 사용한  json-path 정리

$: jsonPath 의 시작점이자 root 가 된다. 가장 바깥으로 생각하자.
 [?(<expression>)] : 필터를 표현할 때 사용한다.	
@: 현재 노드에서 필터를 적용할 때 사용  
        
(https://github.com/json-path/JsonPath) 참고! 
```

### REST API 서비스를 리액티브하게 만들기

```
RestTemplate 은 리액티브를 지원하지 않는다 (비동기 요청)
WebClient 의 인스턴스를 생성(WebClient.create()) 하거나 빈으로 주입받아서 RestTemplate 대신 사용하면 된다

RestService 와 같은 개념
```
```
* 리소스 얻기(GET)

클라이언트가 GET 으로 성분을 얻고 싶을 때 

Mono<Ingredient> ingredient = WebClient.create()
                .get()
                .uri("http://localhost:8080/ingredient/{id}", ingredientId)
                .retrieve() // 어떻게 값을 반환할지 결정 (요청 전송)
                .bodyToMono(Ingredient.class); // Mono<Ingredient>

        ingredient.subscribe(i -> {...});
        
API 를 호출하고 방출된 스트림을 받는다. 그리고 구독해서 데이터를 넘겨준다! 
참고로 구독하면 데이터에서 값을 직접 꺼낼 수 있다. 

  Flux<Ingredient> ingredients = WebClient
                .create()
                .get()
                .uri("http://localhost:8080/ingredients")
                .retrieve()
                .bodyToFlux(Ingredient.class); 

        ingredients.subscribe(i -> {...})

Flux 로 컬렉션 값 가져오기 
```
```
* 오래 실행되는 요청 타임아웃 시키기

Flux<Ingredient> ingredients = WebClient.create()
            .get().uri("http://localhost:8080/ingredients")
            .retrieve()
            .bodyToFlux(Ingredient.class);
        
ingredients.timeout(Duration.ofSeconds(1))
            .subscribe(i -> i.getId(),
                       e -> e.printStackTrace());

해당 요청이 1초를 초과하면 구독의 두 번째 인자로 오류를 만들어서 던질 수 있다. 
```
```
* 리소스 전송하기

Mono<Ingredient> ingredientMono = Mono.just(new Ingredient("1","o", Type.PROTEIN));

        Mono<Ingredient> result = webClient.post()
                .uri("/ingredients")
                .body(ingredientMono, Ingredient.class)
                .retrieve()
                .bodyToMono(Ingredient.class);
        
result.subscribe(i -> {...}); 

put 사용 397 참고 
```
```
* 리소스 삭제하기

Disposable subscribe = webClient.delete()
                .uri("/ingredients/{id}", ingredientId)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
```
### WebClient 로 에러 처리하기
```
에러가 생길 수 있는 Mono 나 Flux 를 구독할 때는 subscribe() 메서드를 호출할 때 
데이터 컨슈머와 에러 컨슈머를 각각 등록해야 한다.

ingredientMono.subscribe(
 i -> { 데이터 처리} , e -> { 에러 발생시 에러 처리})
 
WebClient Api 요청에 오류가 있어서 400 또는 500 오류 코드가 반환되면 두 번째 인자 e 가 실행되고 
WebClientResponseException 를 발생시킨다. 

하지만 이런 포괄적인 에러가 발생하면 무엇이 잘못된 것인지 구체적으로 알 수 없다.
```
```
* 커스텀 에러 핸들러를 추가해서 에러 처리하기!

Mono<Ingredient> ingredientMono = webClient
                .get()
                .uri("http://localhost:8080/ingredients/{id}", ingredientId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        response -> Mono.just(new Exception()))
                .onStatus(HttpStatus::is3xxRedirection,
                        response -> Mono.just(new Exception()))
                .bodyToMono(Ingredient.class);
        
        ingredientMono.subscribe(i -> {...});

onStatus 를 이용하면 api 요청시 발생한 오류코드에 맞는 익셉션을 만들어서 던질 수 있다.
onStatus 는 여러 번 호출할 수 있다! 
```

### 요청 교환하기 

.retreive() 대신 exchange 를 호출하면 더 많은 기능을 사용할 수 있다.
```
exchangeMono() or exchagneFlux() 를 사용하면 반환되는 객체를 모노 타입으로 만들기 전 flatMap 을 이용해서
조건을 추가할 수 있다. 

true 값을 갖는 X_UNAVAILABLE 이라는 헤더가 포함되면 empty Mono 를 반환하기 

Mono<Ingredient> ingredientMono = webClient
             .get()
             .uri("http://localhost:8080/ingredients/{id}", ingredientId)
             .exchangeToMono(c -> {
                 if(c.headers().header("X_UNAVAILABLE").contains("true")){
                     return Mono.empty();
                 }
                 return Mono.just(c);
             })
             .flatMap(c -> c.bodyToMono(Ingredient.class));
```
