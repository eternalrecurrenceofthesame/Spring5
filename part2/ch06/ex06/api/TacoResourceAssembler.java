package tacos.web.api;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import tacos.Taco;

public class TacoResourceAssembler extends RepresentationModelAssemblerSupport<Taco, TacoResource> {

    public TacoResourceAssembler() {
        super(DesignTacoController2.class, TacoResource.class);
    }

    @Override
    public TacoResource toModel(Taco taco) {
        TacoResource tacoResource = new TacoResource(taco);
        return tacoResource;
    }


    @Override
    public CollectionModel<TacoResource> toCollectionModel(Iterable<? extends Taco> tacos) {
        return super.toCollectionModel(tacos);
    }
}
