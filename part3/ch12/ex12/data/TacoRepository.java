package springreactor.tacos.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springreactor.tacos.Taco;

import java.util.UUID;

public interface TacoRepository extends ReactiveCrudRepository<Taco, String> {

    Flux<Taco> findByOrderByCreatedAtDesc();
}
