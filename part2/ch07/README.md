# REST 서비스 이용하기
```
REST 서비스는 클라이언트의 API 요청을 호출해주는 클래스다. RestAPI -> RestService 호출 

REST 서비스는 말 그대로 REST API 를 제공해주는 서비스! 앞서 만든 리소스와 어셈블러는 
REST API 요청을 처리하는 API!
```
## RestTemplate 으로 REST 엔드 포인트 사용하기
```
RestTemplate 은 클라이언트의 REST 서비스 요청을 쉽게 구현할 수 있게 도와주는 템플릿이다.
```
### TacoCloudClient(RestTemplate) 참고
```
* 리소스 가져오기(GET)

rest.getForObject: HTTP GET 요청을 전송하고 응답 몸체와 연결되는 객체를 반환한다.
rest.getForEntity: HTTP GET 요청을 전송하고 ResponseEntity(응답 몸체+객체) 반환
                   getForEntity 는 헤더 응답 정보 같은 더 상세한 응답 콘텐츠가 포함될 수 있다.
                   
rest.exchange:  ResponseEntity(응답 몸체 + 객체) 반환  // getForEntity 와 방식이 약간 다름
```
```
* 리소스 쓰기(PUT)

데이터를 완전히 교체할 때 사용함 반환 타입은 void

rest.put: HTTP PUT
```
```
* 리소스 삭제하기(DELETE)
rest.delte: HTTP DELETE
```
```
* 리소스 데이터 추가하기(POST)

rest.postForLocation: HTTP POST 생성된 URL 반환
rest.postForEntity: HTTP POST ResponseEntity 값을 반환한다.
```

RestTemplate 을 사용하는 메서드들에 대해서 알아봤다. 하지만 RestTemplate 은

하이퍼링크를 포함할 수 없다 하이퍼링크를 포함해야 한다면 Traverson 을 사용해야한다.

### TacoCloudClient(Traverson) 참고

```
* ParameterizedTypeReference<>(){};

Traverson 으로 REST API 를 호출하기 위해서는 타입 정보 <CollectionModel<Ingredient>> 를  
알아야 하는데 자바에서는 런타임시에 제네릭 타입의 정보가 소거되기 때문에 리소스 타입을 지정하기 어렵다!

그래서 

ParameterizedTypeReference<CollectionModel<Ingredient>> ingredientType
                 = new ParameterizedTypeReference<>() {}; // 타입 지정
을 이용해서 리소스 타입을 지정해줘야 한다.

CollectionModel<Ingredient> ingredientsRes = traverson
                 .follow("ingredients")
                 .toObject(ingredientType); // 읽어들이는 데이터의 객체 타입
```
```
Traverson 을 사용하면 HATEOAS 가 활성화된 API 를 이동하면서 해당 API 리소스를 쉽게 가져올 수 있다
하지만 리소스를 쓰거나 삭제하는 메서드를 제공하지 않기 때문에

적절하게 RestTemplate 을 섞어서 사용해야 한다. addIngredient 메서드 

rest.postForObject: HTTP POST 응답 몸체와 연결된 객체 반환
```

