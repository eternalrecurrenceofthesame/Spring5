# REST 서비스 생성하기

## REST 컨트롤러 작성하기
```
스프링 MVC 를 이용한 MPA(다중-페이지 애플리케이션) 을 SPA(단일-페이지 애플리케이션) 방식에서 사용하는
레스트 컨트롤러로 리팩토링하기.

SPA 가 항상 MPA 보다 좋은 선택은 아니다. SPA 를 사용하면 백엔드 기능을 독립적으로 사용하면서 사용자 
인터페이스만 다르게 개발할 수 있다 (ex 모바일 앱) API 를 사용해서 다른 애플리케이션과 유용한 통합이 가능해짐 

모든 애플리케이션에 유연함이 필요한 것은 아님 단순한 정보를 보여주는 것이 전부라면 기존 MPA 가 더 간단한다.
```
```
애노테이션 처리

@GetMapping / HTTP GET / 리소스 데이터 읽기
@PostMapping / HTTP POST / 리소스 생성하기
@PutMapping / HTTP PUT / 리소스 변경하기(전체)
@PatchMapping / HTTP PATCH / 리소스 변경하기(일부)
@DeleteMapping / HTTP DELETE / 리소스 삭제하기
@RequestMapping / 다목적 처리 / 
```

### 서버에서 데이터 가져오기

Rest 컨트롤러를 만들고 외부 애플리케이션과 API 통신해보기!

api.DesignTacoController 참고

```
* Rest Api 애노테이션 설명

@RequestMapping(path = "/design", produce = "application/json")
// 요청 헤더가 제이슨인 경우만 받음
@CrossOrigin(origins = "*") 
// 다른 도메인(프로토콜, 호스트, 포트로 구성) 클라이언트에서 해당 REST API 를사용할 수 있게 해준다.

그 외에 페이징을 사용할 수 있도록 Taco 리포지토리 구현을 PagingAndSortingRepository 로 바꿔줌.
```
```
* 서버에 데이터 전송하기 

@PostMapping(consumes="application/json") 
// 제이슨 타입만 처리
@ResponseStatus(HttpStatus.CREATED) 
// 요청 성공시 HTTP 201(CREATED) 단순 200 보다 세밀하게 응답하자!
@RequestBody Taco taco 
// 제이슨 객체를 타코 객체로 변환, 애노테이션 지정 안하면 MVC ModelAttribute 처럼 동작함
```

```
* 서버의 데이터 변경하기, 간단하게 코드로 설명

PUT - 데이터 전체 변경
@PutMapping("/{orderId}")
    public Order putOrder(@RequestBody Order order){
        return tacoRepository.save(order);
    }

PATCH - 데이터 일부 변경 
@PatchMapping(path="/{orderId}", consumes = "application/json")
public Order patchOrder(@PathVariable("orderId") Long orderId,
                        @RequestBody Order patch){
   Order order = repository.findById(orderId).get();
        
   if(patch.getDeliveryName() != null){
            order.setDeliveryName(patch.getDeliveryName());
        }
        ...
    }
    
패치는 부분 변경의 의미를 지니고 있지만 실제 변경은 직접 코드로 작성해야 한다. 
클라이언트에서 변경하고 싶은 것만 전송하면 패치 로직에서 null 값이 아닐때 변경을 수행한다.
```
```
* PATCH 적용의 여러가지 방법

특정 필드를 null 로 만들 때는 클라이언트에서 이를 나타낼 방법이 필요하다.
컬렉션에 저장된 항목을 삭제 혹은 추가하려면 클라이언트가 컬렉션 전체를 전송해야한다..
```
```
* 서버에서 데이터 삭제하기

@DeleteMapping("/{orderId}")
@ResponseStatus(HttpStatus.NO_CONTENT) // 데이터 반환 x 
    public void deleteOrder(@PathVariable("orderId") Long orderId){
        try{
            orderRepository.deleteById(orderId);
        }catch(EmptyResultDataAccessException e){}
    }

해당 주문이 존재하면 삭제하고 없으면 Empty 예외 발생, 예외가 발생하더라도 단순 
주문이 없다는 예외기 때문에 특별히 할 것은 없음.

ResponseEntity<>(null, HttpStatus.NOT_FOUND); 굳이 반환한다면 not found 
```

## 하이퍼미디어 사용하기
```
HATEOAS(Hypermedia As The Engine Of Application State) 를 이용해서 API 응답으로
호출한 리소스와 관련된 하이퍼링크를 보여줄 수 있다.

클라이언트가 최소한의 URL 을 알고 있으면 반환되는 리소스와 관련해서 처리 가능한 다른 
API URL 을 알아낼 수 있음! 211p
```

### 하이퍼링크 추가하기 

DesignTacoController2 참고

```
* HATEOAS 변경 사항

Resource changed to EntityModel
Resources changed to CollectionModel
ResourceSupport changed to RepresentationModel

PagedResources changed to PagedModel
ResourceAssembler changed to RepresentationModelAssembler
ControllerLinkBuilder changed to WebMvcLinkBuilder
ResourceProcessor changed to RepresentationModelProcessor

ResourceAssemblerSupport to RepresentationModelAssemblerSupport

출처 https://github.com/saechimdaeki
```

```
* @GetMapping("/recent")

CollectionModel<EntityModel<Taco>> 

List<Taco> tacos = tacoRepository.findAll(page).getContent();
CollectionModel<EntityModel<Taco>> recentResources = CollectionModel.wrap(tacos);
// 컬렉션으로 반환받은 타코들을 CollectionModel<EntityModel<Taco>> 타입으로 래핑한다.

Link link = linkTo(mathodOn(DesignTacoController2.class).recentTacos()).withRel("recents");
recentResources.add(link)

return recentResources;
// 그리고 타코리스트 호출 링크를 추가해서 리소스를 반환!

이렇게 하면 API 요청 반환값에서 요청 리소스 값을 보여준다.

"_links": {
    "recents": {
       "href": http://localhost:8080/design/recent"
       }
   }    
```

```
* 리소스 어셈블러 생성하기

리소스 어셈블러와 리소스 객체를 이용하면 도메인을 쉽게 리소스 객체로 바꿔서 
API 응답을 할 수 있다.

도메인을 외부로 노출시키지 않고 API 용 리소스를 만듦으로서 엔티티를 외부에 노출시키지 
않을 수 있으며 필요한 값만 보여줄 수 있다.

참고로 리소스 클래스는 기본 생성자를 가지고 있어야 한다.

TacoResource, TacoResourceAssembler 참고 
IngredientReesource, IngredientResourceAssembler 참고

```
```
* IngredientAssembler 를 활용해서 성분 리소스 보여주기

TacoResource 는 Ingredient 컬렉션 값의 리소스도 보여줘야 한다. 타코 리소스에서 
성분 어셈블러를 주입 받고 어셈블러 메소드를 호출해서 타코에서 꺼낸 성분 컬렉션 값을 전달하면

타코 리소스 필드로 성분 리소스 컬렉션 값을 가질 수 있다.

private static final IngredientResourceAssembler ingredientAssembler
            = new IngredientResourceAssembler();
            
private final CollectionModel<IngredientResource> ingredients;

public TacoResource(Taco taco){
        this.name = taco.getName();
        this.createdAt = taco.getCreatedAt();
        this.ingredients = ingredientAssembler.toCollectionModel(taco.getIngredients());
    }

TacoResource 
```
```
* DesignTacoController 

@GetMapping("/recent")
    public CollectionModel<TacoResource> recentTacos(){
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        List<Taco> tacos = tacoRepository.findAll(page).getContent();
        log.info("taco =" + tacos.get(0).toString());

        CollectionModel<TacoResource> tacoResources = new TacoResourceAssembler().toCollectionModel(tacos);

        Link link = linkTo(methodOn(DesignTacoController2.class).recentTacos()).withRel("recents");
        tacoResources.add(link);

        return tacoResources;
    }

EntityResponse<> 를 리턴 타입으로 받으면 HTTP 상태표시도 지정할 수 있다.
Resource, Assembler 참고
```
```
* embedded 관계 이름 짓기

리소스를 제이슨으로 반환하면 클래스 명에 맞춰서 embedded 이름이 생김 이 이름을 다른 곳에서
사용하고 있는데 클래스 이름을 리팩토링 한다면(그럴 일은 거의 없지만) 사용하는 곳에서 오류가 생긴다.

이를 방지하기 위해 Resource 클래스에 @Relation(value="taco", collectionRelation="tacos) 를 설정하면
클래스 이름이 변경되더라도 제이슨 임베디드 이름은 값을 통일 할 수 있다.

"_embedded" :
    "tacoResourceList":
     -> "tacos" 이렇게 변경됨 통일된 JSON 이름을 가지게 된다.  

```

### 데이터 기반 서비스 활성화 하기

```
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>

이 의존관계를 설정하면 스프링 데이터 리포지토리에 사용되는 엔티티를 API 요청으로
데이터베이스에서 쉽게 호출할 수 있다.

스프링 데이터 REST 를 사용하려면 @RestController 를 비활성화 해야한다.
```
```
* SpringDataRest 사용 예시

localhost:8080/ingredients
localhost:8080/tacoes 

스프링 데이터 API 를 호출하려면 저장 타입을 복수형으로 호출하면 된다. GET 뿐만 아니라
POST,PUT,DELETE 메서드도 지원한다!

기존 레스트 컨트롤러의 경로와 겹치지 않으려면 yml 설정 정보를 알려주면 된다.

spring:
  data:
    rest:
      base-path: /api

localhost:8080/api/ingredints

API 호출시 복수형이 아닌 사용자 이름을 지정하고 싶다면 스프링 데이터 반환 타입 클래스에
@RestResource(rel="tacos", path="tacos") 애노테이션을 사용하면 된다. // Taco 참고
             // 관계 이름, 경로

SpringDataRest 로 타코를 호출하면  타코정보와 스프링 데이터에 저장된 타코의 성분 컬렉션값,
페이징 정보 그리고 각각의 하이퍼링크를 포함한 정보를 받을 수 있다! (매우 엄청난 기능)

```
```
* 페이징과 정렬

localhost:8080/api/tacos?sort=createdAt,desc&page=0&size=12
페이징 정렬 사이즈 지정도 가능! 

HATEOAS 는 처음, 마지막, 다음, 이전 페이지의 링크도 제공한다! 

```
```
* 커스텀 엔드포인트 추가 

앞에서 사용한 페이징과 정렬은 하드코딩 되어 있음 API 스펙이 변경되면 호출되지 않는 문제가 발생한다!
localhost:8080/api/tacos?sort=createdAt,desc&page=0&size=12

RecentTacosController 는 페이징과 정렬 기능을 가진 최근 타코를 호출하는 API 정의다. 
어셈블러와 리소스를 사용해서 엔티티를 외부로 노출하지 않고 캡슐화 할 수 있다.

커스텀 엔드포인트를 추가하면 스프링 데이터 레스트와 함께 사용할 수 있다.

기존에 사용했던 @RestController 가 아닌 @RepositoryRestController 애노테이션은 spring base path 로 
설정한 기본 경로를 가지게 된다. 

그리고 호출할 @GetMapping(path = "/tacos/recent", produces="application/hal+json") API 주소를 설정하면 된다. 

@RepositoryRestController 애노테이션을 사용할 때 주의할 점은
Response 바디에 값을 직접 넣어줘야 하며 // 하이퍼링크를 따로 커스텀 엔드포인트에 추가해줘야 한다

RecentTacosController 참고
```
```
* 커스텀 하이퍼링크를 스프링 데이터 엔드포인트에 추가하기

SpringDataRest 의 엔드 포인트에 커스텀 엔드포인트의 하이퍼링크를 추가할 수 있다.
데이터 레스트를 호출했을 때 원하는 엔드포인트 링크를 추가해서 추가 정보를 제공하는 개념

예를들면 스프링 데이터 레스트로 성분을 조회했을 때 커스텀 엔드포인트 정보까지 볼 수 있게 해준다.

RepresentationModelProcessor<EntityModel<Taco>> 을 구현하고 하이퍼링크를 
스프링 데이터 엔트포인트가 호출할 리소스에 추가해주면 된다. RecentTacosProcessor 참고 
```
