package tacos;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springreactor.tacos.Ingredient;
import springreactor.tacos.Taco;
import springreactor.tacos.data.TacoRepository;
import springreactor.tacos.web.api.DesignTacoController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.*;
import static springreactor.tacos.Ingredient.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DesignTacoControllerTest {

    @Autowired
    private WebTestClient testClient;

    @Autowired
    private WebClient webClient;


    @Test
    public void shouldReturnRecentTacos(){
        Taco[] tacos = {
                testTaco(1L), testTaco(2L),
                testTaco(3L), testTaco(4L),
                testTaco(5L), testTaco(6L),
                testTaco(7L), testTaco(8L),
                testTaco(9L), testTaco(10L),
                testTaco(11L), testTaco(12L),
                testTaco(13L), testTaco(14L),
                testTaco(15L), testTaco(16L)};


        Flux<Taco> tacoFlux = Flux.just(tacos);

        TacoRepository tacoRepository = mock(TacoRepository.class);
        when(tacoRepository.findAll()).thenReturn(tacoFlux);

        WebTestClient testClient = WebTestClient.bindToController(new DesignTacoController(tacoRepository))
                .build(); // TestClient 생성

        testClient.get().uri("/design/recent")
                .exchange() // 최근 타코 요청
                .expectStatus().isOk() // 기대한 응답인지 검사
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(tacos[0].getId().toString())
                .jsonPath("$[0].name").isEqualTo("Taco 1")
                .jsonPath("$[1].id").isEqualTo(tacos[1].getId().toString())
                .jsonPath("$[1].name").isEqualTo("Taco 2")
                .jsonPath("$[11].id").isEqualTo(tacos[11].getId().toString())
                .jsonPath("$[11].name").isEqualTo("Taco 12")
                .jsonPath("$[12]").doesNotExist();
    }

    @Test
    public void shouldSaveATaco(){
        TacoRepository tacoRepository = mock(TacoRepository.class);

        Taco taco = new Taco(1L, "newTaco");
        Mono<Taco> tacoMono = Mono.just(taco);

        when(tacoRepository.save(taco)).thenReturn(tacoMono);

        WebTestClient testClient = WebTestClient.bindToController(new DesignTacoController(tacoRepository))
                .build();

        testClient.post().uri("/design")
                .contentType(MediaType.APPLICATION_JSON)
                .body(tacoMono, Taco.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Taco.class) // 페이로드 값과 저장된 타코 객체가 같은 타입인지 체크
                .isEqualTo(taco);
    }

    private Taco testTaco(Long number){
        Taco taco = new Taco();
        taco.setId(number != null ? number : 1234L);
        taco.setName("Taco " + number);
        List<Ingredient> ingredients = new ArrayList<>();

        ingredients.add(
                new Ingredient("INGA", "Ingredient A", Type.WRAP));

        ingredients.add(
                new Ingredient("INGB","Ingredient B", Type.PROTEIN));

        return taco;
    }

    @Test
    public void shouldReturnRecentTacos2(){
        testClient.get().uri("/design/recent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[?(@.id == 'TACO1')].name")
                .isEqualTo("Carnivore")
                .jsonPath("$[?(@.id == 'TAC02')].name")
                .isEqualTo("Bovine Bounty");
    }




}
