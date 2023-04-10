package tacos.web.api;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import tacos.Ingredient;

public class IngredientResourceAssembler extends RepresentationModelAssemblerSupport<Ingredient, IngredientResource> {

    public IngredientResourceAssembler() {
        super(DesignTacoController2.class, IngredientResource.class);
    }

    // 단일 리소스 반환
    @Override
    public IngredientResource toModel(Ingredient ingredient) {
        IngredientResource ingredientResource = new IngredientResource(ingredient);
        return ingredientResource;
    }

    // 컬렉션 리소스 반환
    @Override
    public CollectionModel<IngredientResource> toCollectionModel(Iterable<? extends Ingredient> ingredients) {
        return super.toCollectionModel(ingredients);
    }



}
