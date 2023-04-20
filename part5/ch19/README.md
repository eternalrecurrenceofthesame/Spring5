# 스프링 배포하기

## 배포 옵션
```
* 자바 애플리케이션 서버에 배포하는 경우

톰캣, 웹스피어, 웹로직, 또는 다른 자바 애플리케이션 서버에 애플리케이션을 배포해야 한다면 WAR 파일로 
애플리케이션을 배포해야 한다.
```
```
* 클라우드에 배포하기

클라운드 파운드리, AWS, MS Azure, Google Cloud Platform 또는 다른 클라우드 플랫폼으로 애플리케이션을 
배포한다면 JAR 를 사용해야 한다. 
```

### WAR 파일 빌드하고 배포하기

```
* TacoInitializerWar 참고

WAR 파일을 배포하기 스프링 부트가 제공하는 SpringBootServletInitializer 를 상속 받고
오버라이딩 하면 된다.

public class TacoInitializerWar extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TacoCloudApplication.class);
    }
}
```
```
* xml 설정 변경 및 실행

	<artifactId>taco-cloud</artifactId>

	<packaging>war</packaging> // war 설정해주기

	<version>0.0.1-SNAPSHOT</version>
	<name>taco-cloud</name>
  
메이븐 패키징 후 배포하면 된다. 앞서 설명했지만 war 를 사용하는 경우는 자바 애플리케이션 서버에서
사용하는 경우이고 이 경우 서버에 맞는 배포 방식으로 배포하면 된다.

war 로 패키징 했더라도 jar 파일 처럼 명령행에서 실행할 수 있다.
java -jar 
```

그외 도커, 클라우드 배포는 각각의 리포지토리에 작성 예정.

