# 스프링 시작하기

## 스프링이란? 

스프링은 애플리케이션 컨텍스트(스프링 컨테이너) 를 제공해서 컴포넌트(빈 객체)들을 생성, 관리 및 의존성 주입(DI) 를 이용해서 

컴포넌트 간 연결을 해준다.

```
* configuration (자바 기반의 구성)

@Configuration - 빈을 스프링 컨테이너에 제공하는 구성 클래스라는 것을 알려준다.
@Bean - 구성 클래스로 지정한다

* autowiring, component scanning (자동-구성)

@Controller, @Service, @Repository, @Autowired
```


