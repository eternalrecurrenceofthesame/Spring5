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

management:
  info:
    env:
      enabled: true     // <- true 설정을 해줘야 동작한다 기본값이 false 

info:
  app:
    email: email
    phone: 1234

GET localhost:8080/actuator/info 간단하게 info 설정 정보를 콜백할 수 있다
```
```
* 애플리케이션의 건강 상태 살펴보기

GET localhost:8080/actuator/health

management:
  endpoint:
    health:
      show-details: always
      
하나 이상의 건강지표(작동 여부)가 down 이면 down 으로 표시된다. 애플리케이션 건강 상태의
상세 정보를 보려면 management.endpoint.health.show-details 옵션을 추가하면 된다.

never(기본값), always(모든 정보), when-authorized(승인된 클라이언트만 접근 가능)

건강지표는 다른 외부 의존성과 무관하게 diskSpace 라는 이름의 파일 시스템 건강 지표를 갖는다.
사용 가능한 디스크 공간을 나타냄. 547p 참고
```

## 구성 상세 정보 보기 
```
* 빈 연결 정보 얻기 /benas

애플리케이션 컨텍스트의 모든 빈을 제이슨으로 나타낸다.
```
```
* 자동-구성 내역 알아보기 /conditions

positiveMatches: 해당 빈이 자동구성 되었음을 나타낸다.
negatvieMatches: 빈을 구성하려고 했지만 실패 했음을 나타낸다. 551p

unconditionalClasses: 조건 없이 구성된 빈을 나타낸다.

@ConditionalOnMissingBean: 해당 빈이 구성되지 않으면 구성되게 한다.
```
```
* 환경 속성과 구성 속성 살펴보기 /env

어떤 환경 속성을 사용할 수 있고, 어떤 구성 속성들이 각 빈에 주입되었는지 파악하기

/env 는 GET, POST DELETE 를 사용할 수 있다 (일시적 변경 및 삭제, 서버재시작시 롤백됨) 

GET localhost:8080/actuator/env/local.server.port (properties 항목 조회)
POST localhost:8080/actuator/env \
      -d'{"name":"tacocloud.discount.code","value":"TACO1234"}' \
      -H "Content-type: application/json"
      {"tacocloud.discount.coud":"TACO1234"}

DELETE localhost:8080/actuator/env
      {"tacocloud.discount.code":"TACOS1234"}
```
```
* HTTP 요청-매핑 내역 보기 /mappings
```
```
* 로깅 레벨 관리하기 /loggers

http://localhost:8080/actuator/loggers/tacos.web.api
상위 패키지 경로부터 하위 패키지 경로로 로깅레벨을 호출할 수 있다.

configuredLevel: 명시적으로 구성된 로깅 레벨
effectiveLevel: 부모 패키지나 루트 로거로부터 상속받을 수 있는 로깅 레벨

POST 를 이용해서 로깅 레벨을 변경할 수 있다! 
{"configuredLevel":"  "}   // 근데 왜 안되지; 
```
```
* 애플리케이션 활동 지켜보기 /httptrace, /threaddump, /heapdump

/heapdump: 메모리나 스레드 문제를 찾는데 사용할 수 있는 gzip 압축 형태의 HPROF 힙 덤프 파일을
다운로드한다. // 책의 범위를 벗어나기 때문에 자세한 설명 x 필요시 찾아보자!

/httptrace: 최근 100 개 요청을 보여준다. 디버깅에 유용, 스프링 부트 Admin 을 사용하면 정보를 실시간으로
           캡쳐해서 사용할 수 있다!
           
/threaddump: 현재 실행중인 스레드 스냅샷을 제공한다. 마찬가지로 스프링부트 Admin 사용하면 실시간 추적 십가능
```
```
* 런타임 메트릭 활용하기 /metrics

메트릭이란 데이터를 시각화해서 보여주는 것을 의미한다. 메트릭은 종류가 굉장히 많아서 하나하나 알아보기는 힘들다.

http://localhost:8080/actuator/metrics/http.server.requests // 요청 메트릭 조회
http://localhost:8080/actuator/metrics/http.server.requests?tag=status:404 // 404 오류 요청 메트릭 조회

http://localhost:8080/actuator/metrics/http.server.requests?tag=status:404&tag=uri:/**
 /** 요청경로에서 404 오류 메트릭 조회 
 
 이런식으로 요청값을 메트릭으로 세분화해서 조회할 수 있다! 포괄적인것에서 구체적인것으로 찾아가면 됨.
 참고로 이 기능도 스프링 부트 Admin 을 사용하면 편리하게 사용 가능하다.
```

## 액추에이터 커스터마이징 하기

액추에이터의 가장 큰 특징은 애플리케이션 요구를 충족하기 위해 커스터마이징 할 수 있다는 것이다!

* actuator 패키지 참고

### /info 엔드포인트에 정보 제공하기
```
* 커스텀 정보 제공자 생성하기 

TacoCountInfoContributor 참고

{"taco-stats":{
   "count": 77
 }}  // 이런식으로 응답 받을 수 있따
```
```
* 빌드 정보를 /info 엔드포인트에 주입하기

<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<executions>
					<execution>
					<goals>
						<goal>build-info</goal>
					</goals>
					</execution>
				</executions>
        
추가시 어떤 버전의 애플리케이션이 실행중이고 언제 빌드되었는지 정확하게 알 수 있따. (플러그인 적용시 빌드 필요)
```
```
* Git 커밋 정보 노출하기
<build> 추가
  <plugins>
  ...
<plugin>
				<groupId>pl.project13.maven</groupId>
				<artifactId>git-commit-id-plugin</artifactId>
				<version>4.9.10</version>
			</plugin>

management:
  info:
    git:
      mode: full // 설정 정보를 주입하면 상세한 커밋 정보를 얻을 수 있다
      
drity: 프로젝트가 빌드되었을 당시에 빌드 디렉터리에 커밋되지 않은 일부 변경사항이 있었음을 나타낸다.
```
### 커스텀 건강 지표 정의하기

스프링 애플리케이션에 통합할 수 있는 많은 외부 시스템의 건강 상태 정보를 제공한다. 그러나 스프링 부트에서

지원하지 않거나 건강 지표를 제공하지 않는 외부 시스템을 사용하는 경우 커스텀 건강 지표를 만들어야 한다.

```
* WackoHealthIndicator 참고

현재 시간을 검사 후 건강 상태를 나타내는 응답 지표를 만들었다. 

실제로는 외부 시스템에 원격 호출을 한 후 받은 응답을 기준으로 건강 상태를 결정하는 것을 고려해서 만들 수 있다.
```

### 커스텀 메트릭 등록하기 
```
* TacoMetrics 참고 

MeterRegistry 를 생성자 주입을 통해서 사용한다.
AbstractRepositoryEventListner 는 리포지토리 이벤트를 가로챌 수 있다.

onAfterCreate 메서드는 타코가 저장될 때 마다 호출된다.

타코가 저장될 때 마다 타코 카운트를 계산하고 사용된 타코의 식자재재의 카운트를 계산하는 메트릭.
```

