package springreactor.tacos.web.api;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springreactor.tacos.Taco;
import springreactor.tacos.data.TacoRepository;

import java.util.Observable;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping(path="/design",produces="application/json")
@CrossOrigin(origins="*") // 다른 도메인(프로토콜, 호스트, 포트로 구성) 의 클라이언트에서 해당 REST API 를사용할 수 있게 해준다.
public class DesignTacoController {

    private TacoRepository tacoRepository;

    @GetMapping("/recent")
    public Flux<Taco> recentTacos(){ // 리액티브 타입
        return tacoRepository.findAll().take(12);
    }

    @GetMapping("/{id}")
    public Mono<Taco> tacoById(@PathVariable("id") Long id){ // 단일값
        return tacoRepository.findById(id);
    }


    /**
     * 두 번 블로킹 하고 값 저장하기
     */
    @PostMapping(consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Taco> postTaco(@RequestBody Taco taco){

        return tacoRepository.save(taco);
    }

    /**
     * 논 블로킹 처리
     */
    /*
    @PostMapping(consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Taco> postTaco(@RequestBody Mono<Taco> tacoMono){
        return tacoRepository.saveAll(tacoMono).next();
    }
     */


}
