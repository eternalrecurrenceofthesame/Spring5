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

design.html

```
간단한 타임리프 정리

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

th:value="${ingredient.id}" // 성분 아이디 값을 보내고 서버로 전송
``` 


