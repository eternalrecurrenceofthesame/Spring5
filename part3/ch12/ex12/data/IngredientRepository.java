package springreactor.tacos.data;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import springreactor.tacos.Ingredient;
public interface IngredientRepository extends ReactiveCrudRepository<Ingredient, String> {

}
