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

### LDAP 기반 사용자 스토어

스프링 시큐리티 저장소에 시큐리티 내용만 전문적으로 정리할 것이기 때문에 보류

### 사용자 인증의 커스터마이징

사용자 데이터를 JPA 를 이용해서 저장하고 처리해보자! 
