# 구성 속성 사용하기
```
스프링 부트의 자동-구성(autoconfiguration) 에 대해서 알아보고 스프링 부트 구성 속성 알아보기
```

## 자동-구성 세부 조정하기
```
자바 기반 구성(@Configruation) 에서 @Bean 애노테이션이 지정된 메서드는 빈의 인스턴스를 생성하고
속성 값도 설정한다.

의존성 라이브러리를 런타임시에 찾아서 사용할 수 있다면 (pom,gradle) 스프링 부트는 해당 빈을 
자동으로 찾아 스프링 애플리케이션 컨텍스트에 생성하고 필요한 속성 값을 가져와서 사용한다.

이때 기본 원천속성 값의 이름을 다르게 지정하고 싶거나 여러개 지정하고 싶다면 *구성 속성*을 사용할 수 있다.
```

### 스프링 환경 추상화 이해하기

스프링 환경 추상화란 구성 가능한 모든 속성을 한 곳에서 관리하는 개념이다. 애플리케이션 컨텍스트의 빈에서

필요로하는 속성들을 한곳에 모아서 스프링 환경에서 필요한 곳에 값을 전달한다.

예를들면 서블릿 컨테이너가 사용하는 포트는 8080 이 값이 원천 속성에 저장되어 있고 스프링 컨테이너가

구동될 때 서블릿 컨테이너??에 8080 포트 설정을 스프링 환경에서 전달해준다. 169 그림참고

```
* 원천 속성 
JVM 시스템, 운영체제 환경변수, 명령행 인자, properties 파일, yml 파일 등이 있다

스프링 부트에 의해 자동으로 구성되는 빈들은 스프링 환경으로부터 가져온 속성들을 사용해서 구성될 수 있다.

properties - server.port=9090
server:
  port: 9090
  
프로퍼티스나 야믈 설정값을 바꾸면 다른 포트를 가져와서 사용하게 된다!(구성 속성)  
```
#### + application.yml(구성 속성) 설명
```
* 데이터 소스 구성하기

spring:
  datasource:
    url:
    username:
    password:
    driver-class-name:
    schema:
    data:
    jndi-name:  
```
```
* 내장 서버 구성하기

server:
  port: 0
포트를 0으로 지정해도 0으로 시작하지는 않고 사용가능한 포트를 무작위로 선택하여 시작된다.
통합 테스트를 실행할 때 유용함. MSA 같이 애플리케이션이 시작되는 포트가 중요하지 않을 때도 유용
```
```
* 내장 서버 HTTPS 활성화

keytool 로 키스토어를 생성해서 https 설정하기

server:
  port: 8443 // 개발용 https 서버에서 많이 사용
  ssl:
    key-store: // 키스토어 파일이 생성된 경로로 설정
    key-store-password: // 키스토어 생성할 때 지정했던 비밀번호 설정
    key-password: 
    
키스토어 경로를 지정할 때 애플리케이션 JAR 파일에 키스토어 파일을 넣는다면
클래스패스를 URL 로 지정하여 참조해야 한다?? 173P
```
```
* 로깅 구성하기

스프링 부트의 구성 속성을 사용해서 yml 로 로깅 구성하기

기본적으로 스프링 부트는 INFO 수준의 로그 메시지를 로그백을 통해서 구성한다.

logging:
  path: // 로그 파일 경로
  file: // 로그 저장할 파일 이름 
  level:
    root:
    org:
      springframework:
        security:
        
org.springframework.security: debug 시큐리티는 이런식으로 표기해도 된다!

간단한 경우 yml 로 구성해도 되는데 보통 xml 을 많이 사용하는듯?
```

```
* 다른 속성의 값 가져오기
greeting:
 welcome: ${spring.application.name}
 
 ${...} 을 이용해서 ... 의 속성 값을 greeting.welcome 에 설정할 수 있다.
```

## 직접 만든 구성 속성 생성하기

스프링 부트가 제공하는 @ConfigrutationProperties 애노테이션을 이용하면 직접 구성 속성을

만들어서 사용할 수 있다.
```
* @ConfigurationProperties 를 이용한 구성 설정 만들기

OrderController 참고

@ConfigurationProperties(prefix="taco.orders") // yml 설정 계층을 만듦

taco:
  orders:
    pageSize:
    
private final int pageSize = 20; // 필드 값으로 페이지 사이즈를 설정
Pageable pageable = PageRequest.of(0, pageSize); // 페이지 사이즈 값을 받아와서 사용

이때 페이지 값을 필드에 설정해서 페이징을 사용해도 되고, 야믈에 글로벌 설정값을 줘도 된다.
```
```
* 구성 속성 홀더 정의하기

구성 속성 전용 클래스를 만들어서 관리해보기 

```

