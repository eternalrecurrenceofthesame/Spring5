# 스프링 통합하기
```
애플리케이션이외부시스템과 연결하여작업을 수행할때 스프링 통합을 사용한다.
ex) 애플리케이션에서 이메일 수신 발신, 외부 API 와 상호작용, 데이터베이스에 쓰는 데이터 처리 등등 
```
## 간단한 통합 플로우 선언하기

```
* 의존성 추가
    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-integration</artifactId>
		</dependency> // 스프링 부트 스타터 인티그레이션 통합 프로우 개발시 필수 항목

		<dependency>
			<groupId>org.springframework.integration</groupId>
			<artifactId>spring-integration-file</artifactId>
		</dependency> // 통합 플로우 파일을 읽거나 통합 플로우로부터
                     파일 시스템으로 데이터를 쓸 수 있는 기능 제공
```
```
* 애플리케이션에서 통합 프로우로 데이터를 전송하는 게이트웨이 생성

FileWriterGateway 참고

@MessagingGateway(defaultRequestChannel="textInChannel") // 메시지 게이트웨이 선언 
 
스프링 데이터와 같은 메커니즘으로 게이트 웨이를 런타임 시점에 생성하라고 알려줌
다른 코드에서 파일에 데이터를 써야할 때 이 인터페이스를 사용한다.

defaultRequestChannel 속성은 해당 인터페이스의 메서드 호출로 생성된 메시지가 이 속성에
지정딘 메시지 채널로 전송된다는 것을 나타낸다.

@Header(FileHeaders.FILENAME) String filename,
페이로드가 아닌메시지 헤더 Stirng 값

String data
메시지 페이로드가 전달된다. (메시지는 메시지 헤더 메타데이터 + 실제 데이터로 구성)
```
```
* 통합 플로우 구성 추가하기

XML, 자바, DSL / 세 가지를 사용해서 구성할 수 있다.
```
```
* 파일-쓰기 통합 플로우 메커니즘

파을-쓰기 게이트웨이 -> 텍스트 입력 채널 -> 대문자 변환기 -> 파일-쓰기 채널 -> 파일 아웃바운드 채널 어댑터

```

### xml 을 사용한 통합 플로우 구성
```
많은 개발자들이 xml 사용을 꺼린다.
```

### Java 로 통합 플로우 구성하기

대부분 xml 대신 자바 구성을 사용한다. 자바로 플로우를 정의하자

```

```




