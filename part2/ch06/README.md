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

레스트 컨트롤러를 만들어서 외부 애플리케이션과 API 통신해보기!

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


