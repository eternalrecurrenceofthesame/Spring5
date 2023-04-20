# 스프링 관리하기

액추에이터의 상위 계층에 프런트 인터페이스를 생성해서 실시간 데이터를 캡처해보자!

## 스프링 부트 Admin 사용하기

스프링 부트 Admin 은 하나 이상의 클라이언트 애플리케이션이 제공하는 액추에이터 데이터를 수집해서 보여준다.

### Admin 서버 생성하기
```
	<dependency>
			<groupId>de.codecentric</groupId>
			<artifactId>spring-boot-admin-starter-server</artifactId>
		</dependency>
    
부트스트랩 클래스에 @EnableAdminServer 를 추가한 후 서버 포트를 9090 으로 변경!
```

### Admin 클라이언트 등록하기 
```
* 2 가지 등록 방법

각 애플리케이션이 자신을 어드민 서버에 등록
어드민 서버가 유레카 서비스 레지스트리를 통해서 서비스 찾기
```

### 애플리케이션이 자신을 어드민 서버에 등록하기
```
<dependency>
			<groupId>de.codecentric</groupId>
			<artifactId>spring-boot-admin-server-client</artifactId>
      <version>2.6.2</version> // 버전 정보를 명시해야 한다.
</dependency>
```
```
* yml 설정 

spring:
  application:
    name: taco-client
  boot:
    admin:
      client:
        url: http//:localhost:9090
```
### 유레카 클라이언트를 사용해서 어드민에 등록하기
```
	<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
      <version>3.1.5</version>
	</dependency>
  
yml 설정은 위와 같다.

유레카 서비스를 유레카 레지스트리 서버에 등록하면 유레카에 등록된 어플리케이션을 스프링 부트 어드민이
자동으로 찾아서 액추에이터 데이터를 보여준다 

## Admin 서버 살펴보기

액추에이터가 제공하는 거의 모든 기능을 Admin 으로 편리하게 조회할 수 있다.

### 애플리케이션의 건강 상태 정보와 일반 정보 보기

사용자 친화적인 환경으로 확인 가능!

### Admin 서버의 보안
```
* 어드민 서버에 로그인 활성화 하기

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

어드민 서버도 스프링 시큐리티를 이용해서 보안을 구성할 수 있다.

spring:
  security:
    user:
      name: admin
      password: 12345
```
```
* 액추에이터로 인증하기

어드민 클라이언트(액추에이터 엔드포인트) 에서 시큐리티로 보안처리(시큐리티를 이용한 액추에이터 보안처리)
를 할 경우 어드민에 인증 정보를 제공하지 않으면 어드민에서 액추에이터 데이터를 수집할 수 없다.

어드민 클라이언트 애플리케이션은 앞서 설명했듯이 직접 등록하거나, 유레카 서버를 통해서 발견될 수 있는데
각각 클라이언트 정보를 제공하는 방법이 다르다.

* 클라이언트를 어드민에 직접 등록할 때 시큐리티 정보 방법
spring:
  boot:
    admin:
      client:
        url: http://localhost:9090
        instance:
          metadata:
            user.name: ${spring.security.user.name}
            user.password: ${spring.security.user.password}
           
* 유레카를 사용해서 어드민에 등록될 때 
eureka:
  instance:
    metadata-map: 
      user.name: 
      user.password:
```

