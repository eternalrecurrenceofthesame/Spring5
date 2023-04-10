package tacos.web.api;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import tacos.Ingredient;
import tacos.Taco;

import java.time.LocalDate;
import java.util.List;

@Relation(value="taco", collectionRelation = "tacos")
@NoArgsConstructor
@Data
public class TacoResource extends RepresentationModel<TacoResource> {

    private static final IngredientResourceAssembler ingredientAssembler
            = new IngredientResourceAssembler();

    private String name;
    private LocalDate createdAt;
    private CollectionModel<IngredientResource> ingredients;


    public TacoResource(Taco taco){
        this.name = taco.getName();
        this.createdAt = taco.getCreatedAt();
        this.ingredients = ingredientAssembler.toCollectionModel(taco.getIngredients());
    }


}
