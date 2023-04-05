# 웹 애플리케이션 개발하기

타코 클라우드를 개발하자! 

## 정보 보여주기 

* 도메인 생성

Taco, Ingredient 참고

* 컨트롤러 생성

DesignTacoController 참고

```
요청-대응 애노테이션

@RequestMapping: 다목적 요청을 처리한다 (기본 경로 지정)
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
@PatchMapping
```

```
주요 메서드

Type[] types = Type.Values(); // enum 을 배열로 만들기 

for(Type type : types){
  model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
} 

pirvate List<Ingredient> filterByType(List<Ingredient> ingredients, Type type){
    return ingredients.stream().filter(i -> i.getType().equals(type)).collect(Collectors.toList());
}

// 타입 값을 소문자로 만들고 필터 메서드로 타입에 해당하는 성분만 걸러서 model 값에 추가!
```

* 뷰 디자인 하기 (타임리프 사용) 

#### + 간단한 타임리프 정리

design.html, orderForm.html 참고

```
${...} // 변수 표현식 모델에 담긴 변수에 직접 접근한다

th:src="@{/images/TacoCloud.png} // static 소스 가지고 오기"

th:object="${taco}" // 모델 객체 받기 폼 태그에서 사용
th:field="*{field}" // object 모델의 필드 값에 접근하기

* th:field 는 정상 상황에서는 모델 객체의 값을 사용하지만, 오류가 발생하면 FieldError 에서 보관한
입력 값을 재사용해서 출력한다.


th:if="${#fields.hasErrors('ingredients')}" // th:if 가 참이면 해당 태그를 출력 거짓이면 출력안함
       ${#fields.hasErrors('ingredients')}  // BindingResult 가 제공하는 검증 오류에 접근해서 입력 오류 체크

th:errors="*{ingredients}" // 해당 필드에 입력 오류가 있으면 오류 메시지를 출력한다.

th:each="ingredient : ${wrap}" // 모델로 받은 wrap 값을 for 로 돌림
th:text="${ingredient.name}"  // 객체 그래프 탐색처럼 성분 객체에서 이름을 꺼낸다

th:value="${ingredient.id}" // 아이디 값을 서버로 전송

th:action="@{/...}" // submit 누르면 해당 URL 호출
``` 
```
타임리프를 이용한 메시지, 국제화 처리

<label for="itemName" th:text="#{item.itemName}"></label> // 타임리프 #{...} 문법 사용

* messages.properties 
item.itemName = 아이템 이름
 
* messages_en.properties
item.itemName = itemName

HTTP accept-language 헤더 값을 통해서 국제화 적용

로케일 정보가 없으면 시스템의 기본 로케일 정보를 호출해서 사용한다 시스템 로케일 정보호출에
실패하면 디폴트 값을 사용한다.

스프링 부트를 사용하면 메시지 소스 빈과 기본 messages 리소스 이름을 사용할 수 있다.
spring.messages.basename=messages // proprties 기본 값
```
그외 styles.css, 메시지 국제화 참고


* 폼 제출 처리하기

OrderController, Order, orderForm.html 참고

## 폼 입력 유효성 검사하기

스프링이 제공하는 자바 빈 유효성 검사 API(JSR-303) 을 이용해서 유효성 검사하기!

(스프링 부트 웹 스타터에 자동으로 추가된다 ?? 57p 왜 없지 )

* 유효성 검사 규칙 선언하기

Taco, Order 참고 javax.validation 애노테이션을 이용한 검사 추가.

```
@Digits(ineger=3, fraction=0, message="Invalid CVV") // 입력 값이 정확히 3 자리 숫자인지 검사
private Strinc ccCVV;
```
* 폼과 바인딩될 때 유효성 검사 수행하는 방법

DesignTacoController, OrderController

```
검증 애노테이션
@Valid(자바 표준), @Validated(스프링 전용) // @Validated 에는 그룹스 기능이 있는데 잘 사용안함 
등록에서 필요한 정보와, 수정에서 필요한 정보가 다르기 때문에 그룹으로 검증하지 않고 각각의 폼을 만들어야 한다.

BindingResult 는 Errors 인터페이스를 상속받아 기능이 조금 더 추가됨 실무에서는 관례상 BindingResult 를 많이 사용
BindingResult 는 @ModelAttribute 값 다음에 위치해야 한다!
```
