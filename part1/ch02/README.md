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

th:src="@{/images/TacoCloud.png} // static 소스 가지고 오기"

th:object="${taco}" // 모델값 받기 
th:field="*{field}" // object 모델에서 필드 값 보여주기

th:if="${#fields.hasErrors('ingredients')}" // th:if 가 참이면 해당 태그를 출력 거짓이면 출력안함
```
