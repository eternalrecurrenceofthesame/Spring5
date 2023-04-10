package tacos.restclient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tacos.Ingredient;
import tacos.Taco;
import tacos.web.api.TacoResource;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API 요청 클라이언트 명세
 */
@Service
@Slf4j
public class TacoCloudClient {

    /**
     * RestTemplate - 빈으로 생성하거나 필드에 선언하면 된다.
     */
    /* @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    } */

    private RestTemplate rest;
    private Traverson traverson;
    public TacoCloudClient() {
        this.rest = new RestTemplate();
        this.traverson = new Traverson(
                URI.create("http://localhost:8080/api"), MediaTypes.HAL_JSON);
    }

    /**
     * RestTemplate
     */

    /** 리소스 가져오기 (GET) */
    public Ingredient getIngredientById1(String ingredientId){
        return rest.getForObject("http://localhost:8080/ingredients/{id}",
                Ingredient.class, ingredientId); // 순서대로 바인딩 방식
    }

    public Ingredient getIngredientById2(String ingredientId){
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("id", ingredientId);
        return rest.getForObject("http://localhost:8080/ingredients/{id}",
                Ingredient.class, urlVariables); // 파라미터 바인딩 방식
    }

    public Ingredient getIngredientById3(String ingredientId){
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("id", ingredientId);

        URI url = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/ingredients/{id}")
                .build(urlVariables);

        return rest.getForObject(url, Ingredient.class);
    }

    public Ingredient getIngredientById4(String ingredientId){
        ResponseEntity<Ingredient> responseEntity =
                rest.getForEntity("http://localhost:8080/ingredients/{id}",
                        Ingredient.class, ingredientId);
        log.info("Fetched time: " + responseEntity.getHeaders().getDate()); //ResponseEntity 를 사용해서 요청 헤더를 로그로 찍기!

        return responseEntity.getBody();
    }

    public List<Ingredient> getAllIngredients(){
        return rest.exchange("http://localhost:8080/ingredients",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Ingredient>>() {})
                .getBody();
    }

    /** PUT examples */
    public void updateIngredient(Ingredient ingredient){
        rest.put("http://localhost:8080/ingredients/{id}",ingredient,ingredient.getId());
    }

    /** DELETE example */
    public void deleteIngredient(Ingredient ingredient){
        rest.delete("http://localhost:8080/ingredients/{id}",
                ingredient.getId());
    }

    /** POST examples */
    public URI createIngredient1(Ingredient ingredient){
        return rest.postForLocation("http://localhost:8080/ingredients",
                ingredient, Ingredient.class);
    }

    public Ingredient createIngredient2(Ingredient ingredient){
        ResponseEntity<Ingredient> responseEntity = rest.postForEntity("http://localhost:8080/ingredients",
                ingredient, Ingredient.class);
        log.info("새로운 리소스가 생성되었습니다. 리소스: " +
                responseEntity.getHeaders().getLocation());

        return responseEntity.getBody();
    }

    /**
     * Traverson
     */
     public Iterable<Ingredient> getAllIngredientsWithTraverson(){
         ParameterizedTypeReference<CollectionModel<Ingredient>> ingredientType
                 = new ParameterizedTypeReference<>() {}; // 리소스 타입 지정을위한 메서드 253p

         CollectionModel<Ingredient> ingredientsRes = traverson
                 .follow("ingredients")
                 .toObject(ingredientType); // 읽어들이는 데이터의 객체 타입 지정

         Collection<Ingredient> ingredients = ingredientsRes.getContent();

         return ingredients;
     }

     public Ingredient addIngredient(Ingredient ingredient){
         String ingredientsUrl = traverson.follow("ingredients")
                 .asLink()
                 .getHref();

         return rest.postForObject(ingredientsUrl,
                 ingredient, Ingredient.class);
     }

     public Iterable<TacoResource> getRecentTacosWithTraverson(){
         ParameterizedTypeReference<CollectionModel<TacoResource>> tacoType
                 = new ParameterizedTypeReference<>() {}; // 리소스 타입 지정을위한 메서드 253p

         CollectionModel<TacoResource> tacoRes = traverson.follow("tacos")
                 .follow("tacos")
                 .follow("recents")
                 .toObject(tacoType);

         return tacoRes.getContent();
     }



}
