# 스프링 시작하기

## 스프링이란? 

스프링은 애플리케이션 컨텍스트(스프링 컨테이너) 를 제공해서 컴포넌트(빈 객체)들을 생성, 관리 및 의존성 주입(DI) 를 이용해서 

컴포넌트 간 연결을 해준다.

```
* configuration (자바 기반의 구성)

@Configuration - 빈을 스프링 컨테이너에 제공하는 구성 클래스라는 것을 알려준다.
@Bean - 구성 클래스로 지정한다

* autowiring, component scanning 을 기반으로 하는 자동-구성

@Controller, @Service, @Repository, @Autowired
```

## 스프링 애플리케이션 초기 설정하기

Tip 클라우드 플랫폼에 배포 하려면 JAR 를 선택해야 한다!

* 애플리케이션의 부트스트랩(구동 클래스 , @SpringBootApplication)
```
@SpringBootApplication 의  3 가지 기능

* @SpringBootConfiguration: 현재 클래스를 구성 클래스로 지정한다. 필요시 자바 기반 구성 클래스 추가 가능
* @EnableAutoConfiguration: 스프링 부트 자동-구성을 활성화한다. 
* @ComponentScan: 컴포넌트 스캔을활성화 한다.

부트 스트랩의 main() 메서드의 역할

JAR 파일이 실행될 때 호출되어 애플리케이션을 시작시키고 컨테이너를 생성하는 SpringApplication.run() 메서드를
호출한다. 메서드 매개 변수로 구성 클래스와 , 명령행 ? command-line 을 받는다
```

## 스프링 애플리케이션 작성하기

* 컨트롤러로 웹 요청 처리하기 - HomeController 참고

* 뷰 정의하기 - home.html 참고 
```
resources

* static: 브라우저에 정적인 콘텐츠(이미지,스타일시트,자바스크립트 등)를 제공하는 폴더
* templates: 콘텐츠를 보여주는 템플릿 파일을 두는 폴더
```

* 컨트롤러 테스트하기 - HomeControllerTest
```
@WebMvcTest(HomeController.class) // HomeController MVC 를 테스트한다는 의미
@Autowired private MockMvc mockMvc; // 모의 MVC 객체 주입

@Test
void testHomepage() throws Exception {
   mockMvc.perform(get("/"))
         .andExpect(MockMvcResultMatchers.status().isOk()) // 200 체크
         .andExpect(MockMvcResultMatchers.view().name("home")) // 뷰 체크
         .andExpect(content().string(Matchers.containsString("Welcome to..."))); // 콘텐츠 String 체크
    }
```

## 스프링 부트 DevTools 알아보기

* 템플릿 캐싱을 비활성화 하여 템플릿이 변경된 경우 새로고침만 하면 변경된 값을 볼 수 있다. 

(의존성은 자동으로 로드되지 않기 때문에 pom.xml 에 추가 변경이 있으면 새로 시작해야 한다)
 
* 자동으로 브라우저를 새로고침하고 템플릿 캐시를 비활성화하기

템플릿 변경하면 브라우저에서 즉시 변경된 결과를 볼 수 있다. LiveReload 플러그인을 웹 브라우저에 추가하면

새로고침 하지 않고 변경 할 수 있다. 28p

* 웹 브라우저에서 사용할 수 있는 H2 콘솔을 자동으로 활성화 해준다. 

http://localhost:8080/h2-console

## 스프링 살펴보기

스프링의 중요한 의존성들 살펴보기

* 핵심 스프링 프레임워크

스프링 MVC, JdbcTempalte, WebFlux

* 스프링 부트

의존성 제공, 자동-구성, 액추에이터, CLI 등등 31p 

* 스프링 데이터

* 스프링 시큐리티

* 스프링 통합과 배치

다른 애플리케이션 또는 같은 애플리케이션의 서로 다른 컴포넌트들의 통합이 필요할 때 

스프링 통합(실시간)과 스프링 배치(시간 기준 트리거)는 스프링 기반 애플리케이션의 통합 패턴을 제공한다.

* 스프링 클라우드


