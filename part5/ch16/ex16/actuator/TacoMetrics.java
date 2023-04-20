package tacos.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;
import tacos.Ingredient;
import tacos.Taco;

import java.util.List;

@Component
@AllArgsConstructor
public class TacoMetrics extends AbstractRepositoryEventListener<Taco> {

    private MeterRegistry meterRegistry;

    @Override
    protected void onAfterCreate(Taco taco) {
        List<Ingredient> ingredients = taco.getIngredients();

        for (Ingredient ingredient : ingredients) {
            meterRegistry.counter("tacocloud","ingredient",ingredient.getId()).increment();
        }
    }
}
