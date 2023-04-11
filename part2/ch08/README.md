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
푸시 모델: 메시지가 수신 가능하게 되면 우리 코드로 자동 전달

JmsTemplate 은 모든 메서드에서 풀 모델을 사용한다.
```
### JmsTemplate 을 사용해서 메시지 수신하기
```
JmsTemplate 은 브로커로부터 메시지를 가져오는 여러 개의 수신 메서드를 제공한다.
이 수신 메서드 들은 send, convertAndSend 메서드의 타입과 대응된다. 

Message receive()
Object ReveiveAndConvert() 
```
```
* tacocloud.order.queue 도착지로부터 Order 객체 가져오는 코드



```

