package tacos.web.api;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import tacos.Taco;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class RecentTacosProcessor implements RepresentationModelProcessor<EntityModel<Taco>> {

    @Override
    public EntityModel<Taco> process(EntityModel<Taco> taco) {

        //Link link = linkTo(methodOn(RecentTacosController.class).recentTacos()).withRel("recents");
        //taco.add(link); <- 이것 아님
        
        taco.add(Link.of("/api/tacos/recent").withRel("recents"));
   

        return taco;
    }
}
