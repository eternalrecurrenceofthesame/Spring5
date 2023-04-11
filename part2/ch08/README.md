# 비동기 메시지 전송하기
```
비동기 메시징을 사용하면 통신 애플리케이션 간 결합도를 낮추고 확장성을 높여준다.
JMS(java message service), RabbitMQ, AMQP, 아파치 카프카 // 스프링 메시지 기반 POJO 지원 
```
## JMS 로 메시지 전송하기

### JMS 설정하기

JMS 아파치 ActiveMQ Artemis 를 사용, 스프링은 아르테미스가 localhost 의 61616 포트를 리스닝 하는

것으로 간주한다.

```
* Artemis yml 개별 설정             // ActiveMq 사용 설정

  artemis:                          //activemq:
    host: artemis.tacocloud.com     // broker-url:
    port: 61617                     // user:
    user: tacoweb                   // password: 
    password: 12345                 // in-memory:
    
아르테미스나 액티브 중 어느 것을 사용하든 브로커가 로컬에서 실행되는 개발 
환경에서는 앞의 속성들을 구성할 필요는 없다.

그러나 액티브를 사용하면 인 메모리 설정을 false 로 해야 한다. 사용제약이 있음.
```

### 메시지 전송하기

JmsTemplate send 메서드를 사용해서 메시지 전송하기
```
* send 전체 로직 

@Override
public void sendOrder(Order order) {

        jms.send("tacocloud.order.queue",new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(order);

                //jms.send(session -> session.createObjectMessage(order)); 간단한 람다식 표현
            }
        });
```
```
* 메시지 도착지 설정

jms:
  template:
    default-destination: tacocloud.order.queue
대부분 기본 도착지를 야믈 설정으로 사용하지만 기본 도착지 말고 다른 곳으로
보내고 싶다면 send() 메서드의 매개 변수로 도착지를 지정해줘야 한다.

@Bean 
public Destination orderQueue(){
return new ActiveMQQueue("tacocloud.order.queue"); // 아르테미스용 ActiveMQQueue }

근데 직접 빈으로 등록하는 것 보다 send 호출할 때 첫 번째 인자에 도착지 이름을 지정해주면 된다..

jms.send(Bean 객체, new MessageCreator() // 빈 사용
jms.send("tacocloud.order.queue",new MessageCreator() // 파라미터 사용

```

### 메시지 변환하고 전송하기

JmsTemplate 이 제공하는 converterAndSend() 메서드를 사용해보자

```
@Override
    public void sendOrder(Order order) {
        jms.convertAndSend("tacocloud.order.queue", order);
    }

order 객체를 Message 객체로 변환한 후 전송한다. 매우 편리!! 
```

### 메시지 변환기 구현하기

전송할 객체를 메시지 객체로 변환해주는 MessageConvert 인터페이스 를 알아보고 구현해보자

메시지 컨버터는 toMessage, fromMessage 두 개의 메서드를 제공한다. 

간단해서 구현하기 쉽지만 스프링이 구현해 둔 컨버터를 사용하면 된다.

```
SimpleMessageConverter: 기본적으로 사용되는 컨버터 이 경우 전송될 객체가 Serializable 인터페이스를 구현해야 한다.
MappingJackson2MessageConverter: Serializable 구현 제약을 피하기 위해 사용하는 컨버터  268p

기본 컨버터 외에 다른 메시지 컨버터를 사용하려면 해당 변환기의 인스턴스를 빈으로 선언하면 된다.
MessageConfig 참고

잭슨 메시지 컨버터를 만들 때 유의할 점은 setTypeIdPropertyName 으로 객체 타입을 지정할 때 패키지 경로를 하드 코딩
해야 한다는 것임 이렇게되면 수신자도 똑같이 하드 코딩된 경로를 가져야함 269p

이런 경우 타입을 임의로 지정하고 Map 객체를 만들어서 타입 매핑을 따로 해주면 된다. 그리고 수신하는 애플리케이션에서
이와 유사한 메시지 컨버터를 만들면 됨. MessageConfig 참고
```

### 후처리 메시지

후처리 메시지란? 메시지 전송 전 필요한 값을 추가하는 것을 의미함 온라인 오프라인 주문을 구분하고 싶을 때

주문 요청 메시지를 전송하기 전 WEB, STORE 정보를 추가할 수 있는 기능을 제공한다!

```
* send 를 사용하는 경우

jms.send("tacocloud.order.queue",
                session -> {
                    Message message = session.createObjectMessage(order);
                    message.setStringProperty("X_ORDER_SOURCE", "WEB");
                    
                    return message;
                });
```
```
* convertAndSend 를 사용하는 경우

 jms.convertAndSend("tacocloud.order.queue", order,
               message -> {
           message.setStringProperty("X_ORDER_SOURCE", "WEB");
           return message;
          });
```

## JMS 메시지 수신하기
```
풀 모델: 메시지를 요청하고 도착할때 까지 기다림
푸시 모델: 메시지가 수신 가능하게 되면 우리 코드로 자동 전달 (리스너)

JmsTemplate 은 모든 메서드에서 풀 모델을 사용한다.
```
### JmsTemplate 을 사용해서 메시지 수신하기
```
* tacocloud.order.queue 도착지로부터 Order 객체 가져오는 코드 (풀모델)

receive 를 사용하면 메시지 컨텐츠 외에 메타 정보도 가져올 수 있다. 메시지 페이로드(순수 데이터)만 필요하다면 
receiveAndConvert 를 사용하면 된다.

JmsOrderReceiver 참고

리스너를 사용하지 않은 풀 모델은 리시버를 호출하고 메시지가 수신될때 까지 기다린다! 
```
```
* 메시지 리스너 선언하기 

receive(), receiveAndConverter() 를 호출해야 하는 풀 모델과 달리, 메시지 리스너는 메시지가 
도착할 때까지 대기하는 수동적 컴포넌트

@JmsListner 을 사용하면 애플리케이션에서 직접 리시브를 호출하지 않는대신 스프링 프레임워크가 
특정 메시지가 도착하는 것을 기다리고 도착하면 해당 메시지에 적재된 객체를 인자로 전달해서

receiverOrdser() 메서드를 호출해준다.

OrderListner 참고

메시지 리스너 자동화 기능이 좋아 보일 수 있지만 무분별하게 주문을 받게되면 병목현상이 생길 수 있다.
과부하가 생기지 않도록 사용자 인터페이스에서 도착하는 주문을 버퍼링 해야한다. 275p

필요에 따라 사용하면 됨 메시지를 즉각즉각 빠르게 처리해야하면 리스너를 쓰면 되고 
메시지 사용자에 맞춰서 메시지가 사용되어야 하면 풀 모델을 사용

JMS 는 자바 애플리케이션에서만 사용할 수 있음 브로커 메시징 시스템을 사용하면 다른 애플리케이션에서도 
사용할 수 있다!
```

## RabbitMQ 와 AMQP 사용하기
```
메시지 전송자는 래빗 브로커에 메시지를 전송한다 메시지는 주소로 지정된 거래소(exchange)로 들어간다.
거래소는 하나 이상의 큐에 메시지를 전달할 책임이 있다. 
메시징 처리는 거래소 타입, 거래소와 큐 간의 바인딩, 메시지의 라우팅 키 값을 바탕으로 처리된다.

거래소와 큐 간의 바인딩은 라우팅 키 값으로 이루어진다. 거래소와의 바인딩에 따라서 큐로 전달 ?? 
https://jin2rang.tistory.com/entry/RabbitMQ%EB%9E%80
```
```
* 거래소의 종류 277 참고

스프링 애플리케이션에서 메시지를 전송하고 수신하는 방법은 거래소 타입과 무관하다.

중요한 것은 메시지는 라우팅 키를 가지고 거래소로 전달되고 메시지는 바인딩 정의를 기반으로
거래소로부터 큐로 전달된다는 것이다.
```

### RabbitMQ 브로커 속성
```
spring:
  rabbitmq:
    host: rabbit.tacocloud.com // 호스트(기본값은 localhost)
    port: 5673 // 포트(기본 값 5672)
    username: user // 브로커를 사용하기 위한 사용자 이름(선택)
    password: 1234 // 브로커를 사용하기 위한 사용자 암호(선택)
```

### RabbitTemplate 을 사용해서 메시지 전송하기 
```
* RabbitOrderMessagingService 참고

RabbitTemplate 을 이용하면 간편하게 메시지를 전송할 수 있다.

@Override
    public void sendOrder(Order order) {
        MessageConverter converter = rabbit.getMessageConverter();
        MessageProperties props = new MessageProperties(); //spring
        
        props.setHeader("X_ORDER_SOURCE", "WEB"); // 메시지 속성 설정

        Message message = converter.toMessage(order, props);
        rabbit.send("tacocloud.order","routing_key", message);
    }

spring: 
  template:
    exchange: tacocloud.orders
    routing-key: kitchens.central
    
거래소 이름과 라우팅 키값을 지정해줄 수도 있다. 거래소를 지정하지 않을 경우 기본값이 됨.   

rabbit.convertAndSend(order); // 간편한 컨버팅과 전송 방법!
```
```
* Rabbit 메시지 변환기 구성하기

기본적으로 메시지 변환은 SimpleMessageConvert 를 사용한다 앞서 설명했지만 Serializable 을 구현해야함
JSON 기반 메시지를 변환하려면 잭슨 컨버터를 구현하면 됨 MessageConfig 참고
```
```
* Rabbit 메시지 속성 설정하기 RabbitOrderMessagingService 참고

 rabbit.convertAndSend(order, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties props = message.getMessageProperties();
                props.setHeader("X_ORDER_SOURCE", "WEB");
                return message;
            }
        });
```

### RabbitMQ 로부터 메시지 수신하기
```
* RabbitTemplate 을 사용해서 메시지 수신하기

메시지를 소비하는 컨슈머는 큐만 알고 있으면 된다. 거래소,라우팅키는 메시지를 큐로 전달할 때 사용되고 
컨슈머는 사용하지 않는다.

  public Order receiveOrder(){
        Message message = rabbit.receive("tacocloud.order.queue", 30000); // 호출즉시 반환하지 않고 30 초 후 반환

     return message != null ? (Order) converter.fromMessage(message) : null;
    }
    
spring:
  rabbitmq:
    template:
      receive-timeout: 30000 
      
RabbitOrderReceiver 참고 
```
```
* 리스너를 사용해서 RabbitMQ 메시지 처리
rabitmq.OrderListner 참고 JMS 에서 사용한 리스너와 다를 게 없다(사용방식)
```
## 카프카 사용하기

### 카프카 사용을 위한 스프링 설정

```
spring:
  kafka:
    bootstrap-servers: // s 주의
      -kafka.tacocloud.com:9092
      -kafka.tacocloud.com:9093
      -kafka.tacocloud.com:9094
      
카프카 클러스터로의 초기 연결에 사용되는 하나 이상의 카프카 서버 설정

앞서 설명했지만 로컬에서 사용할 때는 기본 설정을 사용하면 된다.
카프카 브로커 기본 포트는 9092
```

### KafkaTemplate 사용하기

```
* KafkaTemplate 참고

Spring:
  kafka:
    template:
      default-topic: tacocloud.orders.topic
      
      카프카 기본 토픽 값 설정 
```
```
* 카프카 리스너 작성

카프카는 수신하는 메서드를 제공하지 않기 때문에 리스너로 수신해야한다.

kafka.OrderListner 참고 

메시지의 추가 메타 데이터가 필요한 경우 ConsumerRecord, Message 객체를 인자로 받을 수 있다. 
```
