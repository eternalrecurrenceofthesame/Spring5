# 스프링 클라우드 서비스 탐구하기

하나의 완전한 애플리케이션 기능을 제공하기 위해 함께 동작하는 작고 독립적인 애플리케이션인 마이크로 서비스 개발하기

## 마이크로 서비스 이해하기 

* 마이크로 서비스의 장점
  * 마이크로 서비스는 작고 한정적이라 쉽게 이해할 수 있다.
  * 마이크로 서비스는 크기가 작기 때문에 테스트하기 쉽다.
  * 마이크로 서비스는 다른 MS 와 공유되지 않는 빌드 의존성을 가지므로 라이브러리 충돌이 생기지 않는다.
  * 마이크로 서비스는 독자적으로 메모리할당 및 인스턴스의 수를 조정할 수 있다.(규모)
  * 각 마이크로 서비스별 적용할 테크놀러지를 다르게 선택할 수 있다.
  * 마이크로 서비스는 언제든 프로덕션(실무환경)으로 이양할 수 있다. (MS 별 각각 배포)

마이크로 서비스가 항상 적합한 것은 아니다 규모가 작은 프로젝트일 경우 모놀리틱 구조로 시작해서

규모가 커질때 MS 를 도입하는 것을 고려할 수 있다.

### 서비스 레지스트리 설정하기

스프링 클라우드는 마이크로 서비스를 개발하는 데 필요한 여러개의 부속 프로젝트로 구성된다. 

이중 하나가 스프링 넷플릭스이며, 넷플릭스 오픈 소스로부터 다수의 컴포넌트를 제공한다. 

컴포넌트 중에는 넷플릭스 서비스 레지스트리인 유레카, 로드밸런싱 알고리즘인 리본 등이 있다. 

```
* 유레카란?

유레카는 각각의 마이크로 서비스의 호스트 명과 포트를 등록하는 서비스 레지스트리다. 유레카 또한 하나의 
마이크로 서비스로 분류할 수 있다.
```
```
* 유레카를 이한 마이크로 서비스 간 호출 예시

MS1 이 MS2 를 호출해야 한다면 유레카에서 MS2 의 이름으로 등록된 인스턴스 정보를 찾아서 호출하면 된다.
그리고 MS1 은 MS2 의 어떤 인스턴스를 사용할지 선택해야 한다.

이때 특정 인스턴스를 매번 선택하는 것을 피하기 위해 클라이언트 측에서 동작하는 로드 밸런싱 알고리즘을
적용하는 것이 좋다. 로드밸런싱으로는 넷플릭스 프로젝트인 리본을 사용할 수 있다.

로드 밸런싱이란 서버가 처리해야할 일을 여러 대의 서버로 나누어서 처리하는 것을 말한다! 
```
```
* 클라이언트 측의 로드 밸런서를 사용하는 이유

중앙 집중화된 서비스에서 로드 밸런싱을 하는 것이 일반적이지만 리본은 각 클라이언트에서 실행되는
클라이언트 로드 밸런서다.

클라이언트에서 로드 밸런싱을 하면 클라이언트의 수에 비례해여 로드 밸런서의 크기가 조정되고
모든 서비스에 획일적으로 적용하지 않고 각 클라이언트에 최적화된 밸런싱 알고리즘을 사용할 수 있다.

이런 과정의 대부분은 자동으로 처리해준다. 445p
```

### 유레카 구성하기 
```
* 의존성 설정 및 유레카 서버 활성화

<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

spring:
  config:
    import: "optional:configserver:" // yml 설정 추가 

부트스트랩 클래스에 @EnableEurekaServer 애노테이션을 지정하고 애플리케이션을 실행하면
유레카 웹 대시보드가 나타난다.
```
```
프로덕션 환경에서 유레카 서버는 클러스터로 구성하는 것이 좋다. 유레카 서버에 문제가 발생해도 다른 유레카
서버를 사용할 수 있게 하면 문제가 발생하지 않는다.

유레카는 다른 유레카 서버로부터 서비스 레지스트리를 가져오고, 다른 유레카 서버의 서비스에 자신을 등록해서
사용하면 된다.

실무에서는 클러스터로 사용하면 되지만 개발환경에서는 유레카 서버를 두 개 이상 실행하는 것은 불편하고 불필요하다.

그러나 유레카 서버를 올바르게(클러스터로) 구성하지 않으면 유레카는 30초마다 예외 형태의 로그 메시지를 출력한다.
유레카는 30 초마다 다른 유레카 서버와 통신하면서 자신이 작동 중임을 알리고 레지스트리 정보를 공유함.

오류 로그를 받지 않으려면 유레카 서버가 혼자임을 알도록 알려줘야 한다.
```

### 개발 환경 유레카 yml 설정
```
server:
  port: 8761 # 서버 포트 지정하기 

eureka:
  instance:
    hostname: localhost
  client:
    fetchRegistry: false      # 다른 유레카 서버로부터 정보 가지고 오기
    registerWithEureka: false # 다른 유레카 서버의 서비스로 자신을 등록하기 
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

fetchRegistry, registerWithEureka 는 유레카와 상호작용하는 방법을 알려주기 위해 다른 마이크로
서비스에 설정할 수 있는 속성 (유레카 역시 마이크로 서비스, 두 개의 기본값은 true)

defaultZone 에는 유레카 서버 주소를 등록한다, 유레카 서버는 하나 이상 등록할 수 있다. 448p
```
```
* 자체-보존 모드 비활성화 시키기

유레카 서버는 서비스 인스턴스(유레카 서버를 사용하는 클라이언트? 449p) 에서 자신을 등록하고 갱신 요청을 
30초 마다 전송하기를 기대한다. 
(해당 서비스가 살아 있어서 사용할 수 있는지 확인하기 위함)

일반적으로 3 번의 갱신기간 동안 갱신되지 않으면 해당 인스턴스의 등록을 취소하게 된다.
(해당 서비스 인스턴스가 삭제되어서 사용할수 없게 됨)

이렇게 중단되는 서비스의 수가 임계 값을 초과하면 유레카 서버는 네트워크에 문제가 생긴 것으로 간주하고
레지스트리에 등록된 나머지 서비스 데이터를 보존하기 위한 자체 보존 모드가 되어서 추가적인 서비스 인스턴스의
등록 취소가 방지된다.

프로덕션 환경에서는 true 값을 설정하는 것이 좋지만 유레카 갱신 요청을 받을 수 없는 개발 환경에서는 이 속성을
false 로 하는 것이 유용하다.
서비스 인스턴스의 상태가 자주 변경될 수 있는 개발 환경에서 자체 보존 모드가 활성화되면 중단된 서비스의 등록이
계속 유지되어 다른 서비스가 해당 서비스를 사용하려고 할 때 문제가 생길 수 있다.

eureka:
  server:
    enableSelfPreservation: false
```

### 유레카 확장하기

앞서 설명했지만 개발시에는 단일 유레카 인스턴스가 편리하지만 프로덕션 환경에서는 고가용성을 위해

최소 두 개의 유레카 인스턴스를 가져야 한다.

```
* 프로덕션 환경의 스프링 클라우드 설정 

# 프로덕션 환경 설정 appliation.yml 참고 #

프로덕션 환경에서 유레카를 레플리케이션 하는 방법
https://medium.com/@ali_boussouf/spring-cloud-eureka-replicas-df163ed920fe 
https://www.youtube.com/watch?v=Y3JxNzBSOp0

교재에 있는 내용과 완전히 다르다. 교재에 있는 내용으로는 레플리케이션을 할 수 없다.
대체 왜 안되는 적어둔거지.. 

```
## 서비스 등록하고 찾기

eurekaclient 참고

```
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
```
* 서비스 yml 설정

spring:
  application:
    name: eureka-client-service
    
서비스 이름을 설정한다

server:
  port: 0

서비스 포트를 0 으로 설정하면 각 서비스 애플리케이션 시작시 포트 번호가 무작위로 선택된다.

eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-1-server.com:9001/eureka/, http://eureka-1-server.com:9002/eureka/
      
유레카 서버에 등록되도록 유레카 서버를 지정한다, 서버가 중단되어도 사용할 수 있게 앞서 복사한 
서버를 등록한다.
```
```
* 서비스 사용하기


```

