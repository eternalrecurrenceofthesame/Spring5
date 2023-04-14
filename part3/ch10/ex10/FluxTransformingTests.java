package springreactor;

import lombok.ToString;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class FluxTransformingTests {

    /**
     * 리액티브 타입으로부터 데이터 필터링 하기
     */
    @Test
    public void skipAFew(){
        Flux<String> skipFlux = Flux.just("one","two","skip a few", "ninety nine", "one hundred")
                .skip(3);

        StepVerifier.create(skipFlux)
                .expectNext("ninety nine", "one hundred")
                .verifyComplete();
    }

    @Test
    public void skipAFewSeconds(){
        Flux<String> skipFlux = Flux.just("one","two","skip a few", "ninety nine", "one hundred")
                .delayElements(Duration.ofSeconds(1)) //
                .skip(Duration.ofSeconds(4)); // 4 초 지연 후 방출 값 생성

        StepVerifier.create(skipFlux)
                .expectNext("ninety nine", "one hundred")
                .verifyComplete();
    }

    @Test
    public void take(){
        Flux<String> nationParkFlux =
                Flux.just("YellowStone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
                        .take(3);
    }

    @Test
    public void take2(){
        Flux<String> nationParkFlux =
                Flux.just("YellowStone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
                        .delayElements(Duration.ofSeconds(1))
                        .take(Duration.ofMillis(3500));

        StepVerifier.create(nationParkFlux)
                .expectNext("YellowStone", "Yosemite", "Grand Canyon")
                .verifyComplete();
    }

    @Test
    public void filter(){
        Flux<String> nationalParkFlux =
                Flux.just("YellowStone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
                        .filter(np -> !np.contains(" "));
        StepVerifier.create(nationalParkFlux)
                .expectNext("YellowStone", "Yosemite", "Zion")
                .verifyComplete();
    }

    @Test
    public void distinct(){
        Flux<String> animalFlux = Flux.just("dog", "dog", "cat", "cat", "cat")
                .distinct();

        StepVerifier.create(animalFlux)
                .expectNext("dog", "cat")
                .verifyComplete();
    }


    /**
     * 리액티브 데이터 매핑하기
     */
    @Test
    public void map(){ // 얘는 왜 안되는 거지;;
        Flux<Player> playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .map(n -> {
                    String[] split = n.split("\\s");
                    return new Player(split[0], split[1]);
                });

        StepVerifier.create(playerFlux)
                .expectNext(new Player("Michael","Jordan"))
                .expectNext(new Player("Scottie","Pippen"))
                .expectNext(new Player("Steve","Kerr"))
                .verifyComplete();
    }

    @ToString
    static class Player{
        private String firstName;
        private String lastName;

        public Player(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    @Test
    public void flatMap(){
        Flux<Player> playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .flatMap(n -> Mono.just(n)
                        .map(p -> {
                            String[] split = p.split("\\s");
                            return new Player(split[0], split[1]);
                        }).subscribeOn(Schedulers.parallel())
                );

        List<Player> playerList = Arrays.asList(
                new Player("Michael", "Jordan"),
                new Player("Scottie", "Pippen"),
                new Player("Steve", "Kerr"));
    }
}
