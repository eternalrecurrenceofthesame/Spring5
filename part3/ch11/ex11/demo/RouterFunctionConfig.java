package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import springreactor.tacos.Taco;
import springreactor.tacos.data.TacoRepository;


import java.net.URI;
import java.time.Duration;

import static reactor.core.publisher.Mono.just;

@Configuration
public class RouterFunctionConfig {

    @Autowired
    private TacoRepository tacoRepository;

    @Bean
    public RouterFunction<?> helloRouterFunction(){
        return RouterFunctions.route(RequestPredicates.GET("/hello"),
                ServerRequest -> ServerResponse.ok().body(just("Hello World!"),String.class))
                .andRoute(RequestPredicates.GET("/bye"),
                ServerRequest -> ServerResponse.ok().body(just("See Ya!"), String.class));
    }


    @Bean
    public RouterFunction<?> routerFunction() {
        return RouterFunctions.route(RequestPredicates.GET("/design/taco"), this::recents)
                .andRoute(RequestPredicates.POST("/design"), this::postTaco);
    }

    public Mono<ServerResponse> recents(ServerRequest request){
        return ServerResponse.ok()
                .body(tacoRepository.findAll().take(12), Taco.class);
    }

    public Mono<ServerResponse> postTaco(ServerRequest request){
        Mono<Taco> taco = request.bodyToMono(Taco.class);
        Mono<Taco> savedTaco = tacoRepository.saveAll(taco).next();// saveAll 을 사용해야 한다.

        Long tacoId = savedTaco.block(Duration.ofSeconds(1)).getId(); // 1 초 블록하고 값만 빼온다

        return ServerResponse
                .created(URI.create("http://localhost:8080/design/taco/" + tacoId))
                .body(savedTaco, Taco.class);
    }

}
