# 스프링 시큐리티

## 스프링 시큐리티 구성하기

SecurityConfig 참고 

```
WebSecurityConfigurerAdapter 는 deprecate 됐지만 교재 실습을 위해서 임시적으로 사용
SpringSecurity 는 따로 저장소로 만들 예정이기 때문에 부득이 하게 여기서만 사용하겠음.
```

보안을 테스트할 때는 웹 브라우저를 private, incognito 모드로 설정하고 테스트하자!

## 사용자 정보를 보관할 스토어 만들기

### 인메모리 사용자 스토어

```
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user1")
                .password("{noop}password1") 
                .authorities("ROLE_USER")
                .and()
                .withUser("user2")
                .password("{noop}password2")
                .authorities("ROLE_USER");}
```
스프링 5부터는 반드시 비밀번호를 암호화 해야하지만 {noop} 을 지정하면 암호화 하지 않아도 된다! 

인메모리 사용자 스토어는 테스트 목적이나 간단한 애플리케이션 관리에 사용한다!

### JDBC 기반의 사용자 스토어

사용자 정보는 관계형 데이터베이스로 유지, 관리되는 경우가 많으므로 JDBC 기반의 사용자 스토어가

적합하다.

```
SecurityConfig 참고

@Autowired  // 필드 주입은 스프링 설정을 목적으로 하는 @Configuration 같은 곳에서만 특별한 용도로 사용
DataSource dataSource;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     auth.jdbcAuthentication()
          .dataSource(dataSource);}
```

스프링 시큐리티에서 데이터베이스와 액세스 할 수 있게 DataSource 를 알려준다. (사전 지정)

스프링 시큐리티에서 사전 지정된 데이터베이스 테이블과 SQL 쿼리를 사용하려면 관련 테이블을 생성하고

사용자 데이터를 추가해야 한다 schema.sql 참고 

스프링 시큐리티는 데이터베이스에서 유저를 찾을 때 사용하는 기본적인 쿼리를 제공한다. (따로 안만들어도됨)

하지만 스프링 시큐리티가 제공하는 쿼리와 데이터베이스 컬럼명이 다를때는 직접 만들어 줘야 한다. 126p

```
* 사용자 정보 쿼리 커스터마이징
 auth.jdbcAuthentication()
      .dataSource(dataSource)
      .usersByUsernameQuery(
           "select username, password, enabled from users where username=?")
       .authoritiesByUsernameQuery(
               "select username, authority from authorities where username=?");
```
```
사용자 정보 쿼리를 사용할 때는 다음 규칙을 준수해야 한다

* 매개변수(where 절에서 사용됨) 는 하나이며 username 이어야 한다.
* 사용자 정보 인증 쿼리에서는 username, password, enabled 열의 값을 반환한다.
* 사용자 권한 쿼리에서는 해당 사용자 이름 username 과 부여된 권한 authority 를 
  포함하는 0 또는 다수의 행을 반환한다
* 그룹 권한 쿼리에서는 각각 그룹 id, 그룹 이름, 권한 열을 갖는 0 또는 다수의 행을 반환한다. 

자세한 스프링 시큐리티 내용은 SpringSecurity 리포지토리를 참고하자.
```
```
* 암호화된 비밀번호 사용하기!
비밀번호는 암호화해서 저장해야함!

.passwordEncoder(new BCryptPasswordEncoder()); : bycrypt 를 해싱 암호화 한다.

현재 상태에서는 BCrypt 를 사용해도 로그인이 안됨! 왜냐하면 로그인 화면에서는 암호화로 접근하지만
데이터베이스에서는 평문으로 저장되어 있기 때문! 그렇기 때문에 예제에서는 평문을 사용하겠음.
```
```
* NoEncodingPasswordEncoder 참고
PasswordEncoder 를 구현한 평문 인코더를 만듦!

클라이언트의 요청 비밀번호(rawPassword)와 데이터베이스에서 가지고온 값(encodedPassword)을 
비교해서 인증 체크

.passwordEncoder(new NoEncodingPasswordEncoder()); // SecurityConfig 값 수정.
```
비밀번호를 암호화 하지 않은 것은 테스트할 때만 사용한다!

```
* JDBC 기반 auth 전체 메서드

@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(
                        "select username, password, enabled from users where username=?")
                .authoritiesByUsernameQuery(
                        "select username, authority from authorities where username=?")
                .passwordEncoder(new NoEncodingPasswordEncoder());
}
```

### LDAP 기반 사용자 스토어

스프링 시큐리티 저장소에 시큐리티 내용만 전문적으로 정리할 것이기 때문에 보류

### 사용자 인증의 커스터마이징

사용자 데이터를 JPA 를 이용해서 저장하고 처리해보자! 간단한 유저 정보를 만듦 시큐리티 사용자 

세부 정보에서 사용할 유저 디테일 계약을 구현. User 참고

스프링 데이터로 이름으로 유저를 찾는 간단한 메소드 생성 Userepository 참고

유저 디테일과 스프링 데이터 리포지토리를 이용하는 UserRepositoryUserDetailsService 구현

```
* User, UserRepository, UserRepositoryUserDetailsService 를 바탕으로 시큐리터 설정

SecurityConfig 참고 

@Autowired
private UserDetailsService userDetailsService;

@Bean
public PasswordEncoder encoder(){
  return new BCryptPasswordEncoder();} 
  
빈으로 BCryptPasswordEncoder 를 등록해서 사용할 수 있게 설정   

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth.userDetailsService(userDetailsService).passwordEncoder(encoder());}
  
유저 디테일 서비스와 인코더를 직접 만들고 시큐리티에서 호출할 수 있게 설정을 리팩토링 했다.
```

### 사용자 등록하기

앞서 만든 사용자 명세 시큐리티를 이용해서 사용자를 등록하는 컨트롤러와 폼 뷰를 만들어보기

```
* RegistrationController(사용자 등록 컨트롤러) 

앞서 만든 유저 리포지토리와 빈으로 등록될 패스워드 인코더(BYCrypti 방식) 를 주입받고 폼으로 넘어온
유저 정보를 유저 인스턴스로 만들어서 저장한다 이때 인코더로 인코딩한 값을 비밀번호로 저장! 

RegistrationForm, RegistrationController, registration.html, 참고
```

## 웹 요청 보안 처리하기

보안 규칙을 적용하기! 특정 페이지는 접근 가능하고 특정 페이지는 로그인 해야 접근 할 수 있게 만들어보자!

### 웹 요청 보안 처리하기

웹 요청을 보안처리하기 위해서는 시큐리티체인 or configure(HttpSecurity http) 를 구현해야 함. 앞서 설명했지만

예제를 만들기 때 WebSecurityConfigurerAdapter 를 사용해서 간단하게 만들어 보겠음.

```
* HttpSecurity 를 사용해서 구성할 수 있는 것

HTTP 요청 처리를 허용하기 전 충족되어야 할 특정 보안 조건 구성
커스텀 로그인 페이지 구성
사용자가 애플리케이션 로그아웃을 할 수 있도록 구성
CSRF(URL 추출, 위조요청) 공격으로 부터 보호하도록 구성 
```
```
* 웹 요청 보안 처리하기 SecurityConfig 참고, 150p 
http
     .authorizeRequests()
     .antMatchers("/design","/orders").access("hasRole('ROLE_USER')")// 지정된 경로의 접근 권한을 검사
     .antMatchers("/","/**").access("permitAll") // 모든 접근 허용
     .and()
     .formLogin().loginPage("/login") // 로그인 폼으로 login.html 설정
     .and()
     .logout().logoutSuccessUrl("/") // 로그아웃 Url 설정
     .and().csrf(); //csrf 옵션 활성화

로그인에 성공하면 사용자가 처음 요청한 로그인이 필요한 페이지로 이동한다
```
기타 사용자 설정
```
* 로그인

로그인과 패스워드 이름을 user, pwd 로 변경하면 따로 설정해야 한다.
.loginPage("/login").loginProcessingUrl("/login).usernameParameter("user")...

사용자가 직접 로그인요청을 시도해서 로그인에 성공한 경우 이동할 페이지 지정
.formLogin().loginPage("/login").defaultSuccessUrl("/design")
                        ("/design", true) true 옵션을 주면 어디서 요청했든 항상 design 으로 간다     
```

### CSRF 공격 방어하기

CSRF 란?

로그인한 사용자를 대상으로 URL 요청을 악의적으로 조작해서 공격자 웹 사이트로 이동시키는 것

스프링 시큐리티에는 내장된 CSRF 방어 기능이 있다. CSRF 공격을 막기 위해 HTML 히든 필드로 

CSRF 토큰을 넣고 서버에서 해당 토큰의 요청을 확인한다.

타임리프를 사용하면 form 요소의 속성 중하나를 th 타임리프 속성으로 만들면  토큰을 히든 필드로

넘겨준다.
```
ex) <form method="post" th:action="@{/login}" id="loginForm">
```        
스프링 시큐리티가 지원하는 CSRF 옵션은 절대 비활성화 하지말자!!         

### 사용자 인지하기

서버에서 사용자 인증을 하고 권한을 부여하는 것으로 충분하지 않다! 사용자가 주문 폼을 요청할 때

사용자 정보에 저장된 이름,주소, 등등이 미리 입력되어 있으면 편리하다.

```
* Order
주문 테이블에 사용자 정보 추가

private User user;
```
```
* OrderController 참고

주문 처리 컨트롤러에서 인증된 사용자가 누구인지 결정하기
@AuthenticationPrincipal 을 사용하면 인증된 사용자 정보를 읽어와서 User 를 만들어준다.

@GetMapping("/current") 으로 폼을 요청하면 인증사용자 정보의 값을 읽어와서 사용자 값을 채우고
모델로 값을 넘겨주면 된다.
```
```
* DesignTacoController 참고

타코를 디자인할 때 사용자 이름을 보여주기 위해 디자인 컨트롤러에서도 사용자를 활용했다.
Principal 을 매개변수로 받고 요청 사용자 이름을 찾은 다음 데이터베이스에서 사용자를 불러와서
모델 값으로 넘겨줌!
```

### 로그아웃 버튼을 추가하고 사용자 정보 보여주기

로그아웃 버튼과 사용자 정보를 보여주는 필드를 각 폼에 추가해보겠음

home, design, orderForm html 에 로그 아웃 추가.
