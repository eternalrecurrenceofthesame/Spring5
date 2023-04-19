# 스프링 부트 액추에이터 사용하기

스프링 액추에이터를 사용해서 애플리케이션 내부 상태를 알아보자 ! 

## 액추에이터 개요
```
* 액추에이터를 통해 알 수 있는 것들

애플리케이션 환경에서 사용할 수 있는 구성속성

패키지의 로깅 레벨
애플리케이션이 사용중인 메모리

지정된 엔드포인트가 받은 요청 횟수
애플리케이션의 건강 상태 정보

기본적인 액추에이터 엔드포인트 540p
```
```
* 액추에이터 기본 경로 설정하기

management:
  endpoints:
    web:
      base-path: /management   // 디폴트 값은 /actuator 이다 
      
건강 상태 정보를 얻고 싶으면 /management/health GET 을 요청하면 된다 
```

### 액추에이터 엔드포인트의 활성화와 비활성화

대부분의 액추에이터 EP 는 민감한 정보를 제공할 때 보안 처리를 해야하기 때문에 비활성화 되어 있다.
```
management:
  endpoints:
    web:
      base-path: /management 
      exposure:
        include: health, info, beans, conditions 
        
이렇게 활성화 할 수 있다. 
 '*' 와일드 카드 사용시 모든 액추에이터 EP 가 노출된다.

include: '*'
exclude: threaddump, headdump 

이런식으로 모두 노출하고 제외할 수 도있다
```

## 액추에이터 엔드포인트 소비하기 

스프링 액추에이터 메서드를 사용하면서 알아보자 !

https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html 참고..

```
* 애플리케이션 정보 요구하기

info:
  app:
    email: email
    phone: 1234

GET localhost:8080/actuator/info 간단하게 info 설정 정보를 콜백할 수 있다
```
```
* 애플리케이션의 건강 상태 살펴보기

GET localhost:8080/actuator/health

```
