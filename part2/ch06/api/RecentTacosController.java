package tacos.web.api;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import tacos.Taco;
import tacos.data.TacoRepository;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@AllArgsConstructor
@RepositoryRestController // 스프링 데이터 레스트의 기본 경로가 추가됨
public class RecentTacosController {

    private TacoRepository tacoRepository;

    @GetMapping(path = "/tacos/recent", produces="application/json")
    public ResponseEntity<CollectionModel<TacoResource>> recentTacos(){
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        List<Taco> tacos = tacoRepository.findAll(page).getContent();

        CollectionModel<TacoResource> tacoResources = new TacoResourceAssembler().toCollectionModel(tacos);

        Link link = linkTo(methodOn(RecentTacosController.class).recentTacos()).withRel("recents");
        tacoResources.add(link);

        return new ResponseEntity<>(tacoResources, HttpStatus.OK);
    }
}
