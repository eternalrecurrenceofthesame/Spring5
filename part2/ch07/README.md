# REST 서비스 이용하기
```
REST 서비스는 클라이언트의 API 요청을 호출해주는 클래스다. 마이크로 서비스에서 
외부 클라이언트가 서비스 인터페이스에 API 요청을 하면 REST 서비스가 호출되고 API 요청을 처리한다.

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



