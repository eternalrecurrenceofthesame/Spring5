package tacos.web.api;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tacos.Taco;
import tacos.data.TacoRepository;

import java.util.Optional;

@AllArgsConstructor
//@RestController // @Controller + @ResponseBody
//@RequestMapping(path = "/design", produces = "application/json") // 헤더의 제이슨 요청만 받음.
@CrossOrigin(origins = "*") // 다른 도메인(프로토콜, 호스트, 포트로 구성) 의 클라이언트에서 해당 REST API 를사용할 수 있게 해준다.
public class DesignTacoController {

    private TacoRepository tacoRepository;

    @GetMapping("/recent")
    public Iterable<Taco> recentTacos(){
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());

        return tacoRepository.findAll(page).getContent();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id){
        Optional<Taco> taco = tacoRepository.findById(id);
        if(taco.isPresent()){
            return new ResponseEntity<>(taco.get(), HttpStatus.OK); // 200
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping(consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco(@RequestBody Taco taco){
        return tacoRepository.save(taco);
    }




}
