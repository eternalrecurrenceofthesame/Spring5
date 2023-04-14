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
* 애플리케이션에서 통합 플로우로 데이터를 전송하는 게이트웨이 생성

FileWriterGateway 참고

@MessagingGateway(defaultRequestChannel="textInChannel") // 메시지 게이트웨이 선언 
 
스프링 데이터와 같은 메커니즘으로 게이트 웨이를 런타임 시점에 생성하라고 알려줌
다른 코드에서 파일에 데이터를 써야할 때 이 인터페이스를 사용한다.

defaultRequestChannel 속성은 해당 인터페이스의 메서드 호출로 생성된 메시지가 이 속성에
지정된 메시지 채널로 전송된다는 것을 나타낸다.

@Header(FileHeaders.FILENAME) String filename,
페이로드가 아닌메시지 헤더 Stirng 값

String data
메시지 페이로드가 전달된다. (메시지는 메시지 헤더 메타데이터 + 실제 데이터로 구성)
```
```
* 통합 플로우 구성 추가하기

XML, 자바, DSL / 세 가지를 사용해서 구성할 수 있다.
```

### xml 을 사용한 통합 플로우 구성
```
많은 개발자들이 xml 사용을 꺼린다.
```

### Java 로 통합 플로우 구성하기

대부분 xml 대신 자바 구성을 사용한다. 자바로 플로우를 정의하자

```
* FileWriterIntegrationConfig 참고

파일 변환기, 파일-쓰기 메시지 핸들러를 빈으로 등록한다. 

게이트웨이를 통해서 데이터(메타데이터 + 페이로드)가 textInChannel(인바운드 채널) 로 전송되고 
인바운드 채널에서 파일 변환기 toUpperCase 를 호출해서 메시지를 대문자로 변환한다.

변환된 결과는 변환기가 지정한 아웃바운드 fileWriterChannel 파일-쓰기 채널로 전달된다 
파일-쓰기 채널은 변환기와 아웃바운드 채널 어댑터를 연결하는 전달자의 역할을 수행한다.


Tip
핸들러에서 응답 채널을 사용하지 않으면 플로우가 정상적으로 작동하더라도 응답 채널이 구성되지 않았다는
로그 메시지가 나타난다.

Tip
채널을 별도로 선언하지 않으면 채널은 자동으로 생성된다. 각 채널의 구성을 제어하고 싶다면 별도의 빈을 만들면 됨

@Bean
public MessageChannel textInChannel() or fileWriterChannel(){
	return new DirectChannel();
}
```

### 스프링 통합의 DSL 구성 사용하기

```
* FileWriterIntegrationDSLConfig 참고 

자바 구성에비해 람다식을 사용하여 코드가 더 간결해진다 ! 

      return IntegrationFlows
                .from(MessageChannels.direct("textInChannel")) // 인바운드
                .<String, String>transform(t -> t.toUpperCase()) // 변환기 선언
                .handle(Files.outboundAdapter(new File("/tmp/sia5/files"))
                        .fileExistsMode(FileExistsMode.APPEND)
                        .appendNewLine(true)).get();

따로 인바운드에서 호출하는 아웃바운드 채널을 만들지 않고 바로 핸들러를 호출할 수 있다.
여기서도 마찬가지로 따로 채널 빈을 선언할 필요는 없다.


아웃바운드 채널을 별도로 구성할 필요가 있다면 추가해주면 된다.
.<String, String>transform(t -> t.toUpperCase()) // 변환기 선언
.channel(MessageChannels.direct("fileWriterChannel"))

DSL 사용시 코드 가동성을 위한 들여쓰기를 한다
통합 플로우 코드가 복잡해지면 플로우의 일부분을 별도 메서드나 서브 플로우로 추출하자! 
```

## 스프링 통합의 컴포넌트 살펴보기 

통합 플로우는 하나 이상의 컴포넌트로 구성된다
```
* 컴포넌트

채널: 메시지 전달

필터: 조건에 맞는 메시지가 플로우를 통과
변환기: 메시지 값을 변경, 페이로드의 타입을 다른 타입으로 변환

라우터: 여러 채널 중 하나의 채널로 데이터를 전달해주는 역할 주로 메시지 헤더 기반
분배기: 메시지를 두 개 이상으로 분리해서 각각의 채널로 전송
집적기: 별개의 채널로부터 전달되는 다수의 메시지를 하나의 메시지로 결합

서비스 액티베이터: 자바 메서드에 메시지를 넘겨준 후 메서드의 반환 값을 출력 채널로 전송

채널 어댑터: 외부 시스템에 채널을 연결한다. 외부로부터 입력받거나 쓸 수 있다.
게이트웨이: 인터페이스를 통해 통합 플로우로 데이터를 전달한다.
```

### 메시지 채널
```
메시지 채널은 스프링 통합 플로우의 컴포넌트 간 데이터를 전달하는 통로
메시지 채널은 통합 파이프라인을 통해서 메시지가 이동하는 수단! (채널을 통해서 통합 플로우로 이동)
```
```
* 채널 구현체

PublishSubscribeChannel: 하나 이상의 컨슈머로 전달 컨슈머가 여럿일 때 모든 컨슈머가 수신 
PriorityChannel: 큐와 유사하지만 FIFO 대신 priority 헤더 기반으로 컨슈머가 메시지를 가져간다.
RendezvousChannel: 큐와 유사하지만 컨슈머가 메시지를 수신할 때까지 전송자가 채널을 차단한다.
DirectChannel: 기본 사용 채널, 전송자와 동일한 스레드로 실행되는 컨슈머를 호출하여 단일 
               컨슈머에게 메시지를 전송한다. (트랜잭션 지원)
ExecutorChannel: TakeExecutor 를 통해서 메시지가 전송된다(전송자와 다른 스레드에서 처리)
		 트랜잭션 지원 x
FluxMessageChannel: 리액티브기반 채널
```

```
* QueueChannel

컨슈머가 메시지를 가져갈 때까지 큐에 메시지가 저장된다. (FIFO 방식) 컨슈머가 여럿이면 하나의 컨슈머만 해당 메시지를 수신한다.
큐 채널 사용시 컨슈머가 이 채널을 폴링(메시지를 지속적으로 확인함) 하도록 구성하는 것이 중요하다

@Bean // 큐 채널 생성
public MessageChannel orderChannel(){
	return new QueueChannel();}

@ServiceActivator(inputChannel="orderChannel"
		  poller=@Poller(fixRate="1000")) 폴링 구성 				
```

### 필터
```
필터는 통합 파이프라인의 중간에 위치한다. 플로우의 전 단계부터 다음 단계로의 메시지 전달을 허용 또는 불허한다.
```
```
* 필터 구현

정숫값을 갖는 메시지를 numberChannel로 입력, 짝수인 경우에만 evenNumberChannel 채널로 전달

자바 구성 사용

@Filter(inputChannel="numberChannel",
	outputChannel="evenNumberChannel")
public boolean evenNumberFilter(Integer number){
return number % 2== 0;} // true false 로 채널 변경 ! 

DSL 사용

@Bean
public IntegrationFlow evenNumberFlow(AtomicInteger integerSource){
	return IntegrationFlows
	...
	.<Integer>filter((P) -> P % 2 == 0) // 필터 메서드가 GenericSelector 를 인자로 받는다.
	...
	.get();
}

DSL 사용시 인바운드 다음에 필터를 적용
```

### 변환기

앞서만든 변환기를 말하는 것!  

```
* 로마 문자로 바꾸기 

@Bean
@Transformer(inputChannel="numberChannel",
	     outputChannel="romanNumberChannel")
public GenericTransformer<Integer, String> romanNumTransformer(){
return RomanNumbers::toRoman;}

@Bean
public IntegrationFlow transformerFlow(){
return IntegrationFlows.
...
.transform(RomanNumbers::toRoman)
...
.get();}

여기서 사용한 로만 넘버스는 따로 만든 클래스를 참조해서 사용한것! 스프링 타입 컨버터 참고.

변환기를 별도의 자바 클래스로 만들 만큼 복잡하다면 스프링 통합 컨피그에 컨버터 클래스를 빈으로 만들고
메서드의 인자로 사용해도 된다.

변환기는 채널-> 필터,라우터 다음에 사용
```

### 라우터

전달 조건을 기반으로 **플로우 내부**를 분기한다.(채널에서 분기되어 서로 다른 채널로 메시지 전달)

```
* RouterConfig 참고 

정숫값을 전달해서 짝수와 홀수를 나눠서 플로우 하는 로직 
```

### 분배기

splitter 패키지 참고 

```
* 분배기 사용 예시

메시지 컬렉션을 나눠서 처리하고 싶을 때
ex) 제품 리스트 컬렉션의 제품을 따로따로 처리하기

연관된 정보를 함께 전달하는 하나의 메시지 페이로드를 두 개 이상의 서로 다른 타입으로 나눌 때 
ex) 주문에서 메시지 대금 청구 정보, 주문 항목 리스트 나누기 
```

```
* 분배기 실행 정리 splitter 참고 

스플리터를 수행할 OrderSplitter 클래스를 만든다. 메시지를 나누기 위해 컬렉션으로 메시지를 나누는 클래스.
@Spliter 애노테이션을 이용해서 스플리터를 설정으로 등록, 라우터를 만들고 스플리터에서 채널로 라우터 플로우와 연결한다.

라우터의 PayloadTypeRouter 는 각 페이로드 타입을 기반으로 서로 다른 채널에 메시지를 전달한다.
```
```
* LineItems 컬렉션을 별도로 처리하고 싶다면?

하나의 메시지에서 스플릿된 컬렉션 메서드를 처리하고 싶다면 라우터에서 넘어온 메시지를 처리하는 
플로우를 만들면 된다. 플로우에서 컬렉션 메서드를 별도로 사용하고 아웃바운드 어댑터로 넘겨준다! 
```
```
* 스플리터 DSL 예시

OrderSplitterDSLConfig 참고
```

### 서비스 액티베이터

서비스 액티베이터는 메시지를 수신하고 핸들러 인터페이스를 구현한 클래스(빈) 에 전달하는 역할 

한마디로 핸들러 호출전에 거치는 단계임.

서비스 액티베이터 기능을 수행하기 위해 커스텀 클래스를 제공해야 할 때가 있다.

```
* 서비스 액티베이터 예시

@Bean
    @ServiceActivator(inputChannel = "someChannel")
    public MessageHandler sysoutHandler(){
        return message -> {
            System.out.println("Message payload: " + message.getPayload());
        };
    }

핸들러를 호출하지 않고 액티베이터에서 콘솔에 값을 찍는 로직 

   @Bean
    @ServiceActivator(inputChannel = "orderChannel",
                      outputChannel = "completeChannel")
    public GenericHandler<Order> orderHandler(OrderRepository orderRepository){
        return (payload, headers) ->{
            return orderRepository.save(payload);
        };
    } 

메시지를 가로채서? 데이터를 저장하고 저장된 객체를 출력 채널로 전달할 수도 있다. 

  @Bean
    public IntegrationFlow fileWriterFlow() {
        return IntegrationFlows
                .from(MessageChannels.direct("textInChannel")) // 인바운드
                .handle(msg ->{System.out.println("Message payload: " + msg.getPayload());}
                        ).get();
    }
    
      @Bean
    public IntegrationFlow fileWriterFlow() {
        return IntegrationFlows
                .from(MessageChannels.direct("textInChannel")) // 인바운드
                .<Order>handle((payload, headers)->{return orderRepository.save(payload);}
                        ).get();
    }
    
DSL 을 이용해서 콘솔, 
```

### 게이트웨이

게이트웨이는 통합 파이프라인의 시작점임 게이트웨이는 단방향 또는 양방향으로만들 수 있다.
```
FileWriterGateway 단방향 게이트웨이 
UpperCaseGateway 양방향 게이트웨이 참고.
```

### 채널 어댑터

채널 어댑터는 통합 플로우의 입구와 출구, 인바운드 채널 어댑터를 통해 통합 플로우로 들어오고

아웃바운드 채널 어댑터를 통해 통합 플로우에서 나가게 된다.

아웃바운드 어댑터는 통합 플로우의 끝단이고 최종 메시지를 애플리케이션이나 다른 시스템으로 넘겨준다.

어댑터(핸들러) 포괄적인 개념인듯 

* 간단한 정리
```
포괄적으로 정리하면 게이트웨이와 통합 파이프라인으로 나눌 수 있다 ?? (책의 설명으로 유추함..)
통합 파이프라인은 파이프라인(채널)과 통합 플로우(컴포넌트)로 나뉜다

채널에서 사용하는 채널 어댑터는 통합 플로우의 입구와 출구가 되고 채널을 통해서 통합 플로우에
접근할 수 있다.

```
```
* 인바운드 어댑터 예시 

 @Bean
    @InboundChannelAdapter(
            poller=@Poller(fixedRate="1000"), channel="numberChannel")
    public MessageSource<Integer> numberSource(AtomicInteger source){
        return () -> {
            return new GenericMessage<>(source.getAndIncrement());
        };
    }

매초 마다 한번씩 숫자를 전달하는 어댑터 

DSL 에서는 from 절이 인바운드 어댑터 역할을 한다.(이건 예제가 안 됨..)
```
```
* 통합 파일 엔드포인트 모듈 어댑터 예제

@Bean
    @InboundChannelAdapter(channel = "file-channel",
    poller=@Poller(fixedDelay="1000"))
    public MessageSource<File> fileReadingMessageSource(){
        FileReadingMessageSource sourceReader = new FileReadingMessageSource();
        sourceReader.setDirectory(new File("input_dir"));
        sourceReader.setFilter(new SimplePatternFileListFilter("file_pattern"));
        return sourceReader;
    }

@Bean
    public IntegrationFlow fileReaderFlow(){
        return IntegrationFlows.from(Files.inboundAdapter(new File("dir"))
                .patternFilter("file_fattern")).get();
    }
DSL 사용

Tip 어댑터 호출전에 사용되는 서비스 액터베이터를 아웃바운드 채널 어댑터 대용으로 자주 사용한다.
```

### 엔드포인트 모듈

스프링 통합은 커스텀 채널 어댑터(인바운드,아웃바운드)를 생성할 수 있는 모듈을 지원 해준다. 

## 이메일 통합 플로우 생성하기 
```
이메일 통합 플로우를 이용해서 외부로부터 이메일 주문을 받고 Order 로 변환한 다음 아웃바운드 채널 어댑터로
API 서비스를 호출해보기
```
```
* tacocloudemail 참고

```

