package springreactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FluxBufferingTests {

    @Test
    public void buffer(){
        Flux<String> fruitFlux = Flux.just("apple", "orange", "banana","kiwi","strawberry");

        Flux<List<String>> bufferedFlux = fruitFlux.buffer(3);

        StepVerifier
                .create(bufferedFlux)
                .expectNext(Arrays.asList("apple","orange","banana"))
                .expectNext(Arrays.asList("kiwi","strawberry"))
                .verifyComplete();
    }

    @Test
    public void buffer_병행_처리(){
        Flux.just("apple","orange","banana","kiwi","strawberry")
                .buffer(3)
                .flatMap(x -> Flux.fromIterable(x)
                        .map(y -> y.toUpperCase())
                        .subscribeOn(Schedulers.parallel())
                        .log()).subscribe();
    }

    @Test
    public void collectList(){
        Flux<String> fruitFlux = Flux.just("apple", "orange","banana","kiwi","strawberry");
        Mono<List<String>> fruitListMono = fruitFlux.collectList();

        StepVerifier
                .create(fruitListMono)
                .expectNext(Arrays.asList("apple","orange","banana","kiwi","strawberry"))
                .verifyComplete();
    }

    @Test
    public void collectMap(){
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");

        // charAt 0 으로 첫 글자만 뽑아서 사용한다.
        Mono<Map<Character, String>> animalMapMono = animalFlux.collectMap(a -> a.charAt(0));

        StepVerifier
                .create(animalMapMono)
                .expectNextMatches(map -> {
                    return
                            map.size() == 3 &&
                                    map.get('a').equals("aardvark") &&
                                    map.get('e').equals("eagle") &&
                                    map.get("k").equals("kangaroo");
                }).verifyComplete();
    }

    /**
     * 오퍼레이션 로직 수행
     */
    @Test
    public void all(){ // 모든 동물 이름에 a 가 포함되어 있는지 체크
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");

        Mono<Boolean> hasAMono = animalFlux.all(a -> a.contains("a"));
        StepVerifier.create(hasAMono)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void any(){ // 최소 하나의 항목이 일치하는지 테스트
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");

        Mono<Boolean> hasTMono = animalFlux.any(a -> a.contains("t"));

        StepVerifier.create(hasTMono)
                .expectNext(true)
                .verifyComplete();
    }

}
