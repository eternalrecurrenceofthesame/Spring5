package springreactor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FluxCreationTests {

    /**
     * 객체로부터 생성하기
     */
    @Test
    public void createAFlux_just(){
        Flux<String> fruitFlux =
                Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");

        /** 구독자 추가 메서드 .subscribe
         *  Consumer 를 사용해서 Subscriber 객체를 생성한다
         *
         *  subscribe() 를 호출하는 즉시 데이터가 전달된다. 중간에 다른 오퍼레이션이 없으므로
         *  데이터는 Flux -> Subscriber 로 전달된다.
         */
        fruitFlux.subscribe(f -> System.out.println("Here's some fruit: " + f));



        /**
         * StepVerifier 는 해당 리액티브 타입을 구독한 후 스트림을 통해 전달되는 데이터에 assertion 을 적용,
         * 해당 스트림이 기대한 대로 작동하는지 검사.
         */
        StepVerifier.create(fruitFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .expectNext("Grape")
                .expectNext("Banana")
                .expectNext("Strawberry")
                .verifyComplete();
    }

    @Test
    public void createAFlux_fromArray(){
        String[] fruits = new String[]{
                "Apple", "Orange", "Grape", "Banana", "Strawberry"};

        Flux<String> fruitFlux = Flux.fromArray(fruits);

        StepVerifier.create(fruitFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .expectNext("Grape")
                .expectNext("Banana")
                .expectNext("Strawberry")
                .verifyComplete();

    }

    @Test
    public void createAFlux_fromIterable(){
        ArrayList<String> fruitList = new ArrayList<>();
        fruitList.add("Apple");
        fruitList.add("Orange");
        fruitList.add("Grape");
        fruitList.add("Banana");
        fruitList.add("Strawberry");

        Flux<String> fruitFlux= Flux.fromIterable(fruitList);

        StepVerifier.create(fruitFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .expectNext("Grape")
                .expectNext("Banana")
                .expectNext("Strawberry")
                .verifyComplete();
    }

    @Test
    public void createAFlux_fromStream(){
        Stream<String> fruitStream = Stream.of("Apple", "Orange", "Grape", "Banana", "Strawberry");
        Flux<String> fruitFlux = Flux.fromStream(fruitStream);

        StepVerifier.create(fruitFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .expectNext("Grape")
                .expectNext("Banana")
                .expectNext("Strawberry")
                .verifyComplete();
    }

    /**
     * Flux 데이터 생성하기
     */
    @Test
    public void createAFlux_range(){
        Flux<Integer> intervalFlux = Flux.range(1, 5);

        StepVerifier.create(intervalFlux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    public void createAFlux_interval(){
        Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(1))
                .take(5);

        StepVerifier.create(intervalFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .verifyComplete();
    }






}
