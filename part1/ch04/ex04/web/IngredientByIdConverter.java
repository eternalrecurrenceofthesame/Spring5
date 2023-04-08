package tacos.web;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tacos.Ingredient;
import tacos.data.IngredientRepository;

/**
 * 데이터베이스의 식자재 데이터를 Ingredient 객체로 변환하기
 */
@AllArgsConstructor
@Component
public class IngredientByIdConverter implements Converter<String, Ingredient> {

    private final IngredientRepository ingredientRepository;

    @Override
    public Ingredient convert(String id) {
        return ingredientRepository.findById(id).orElse(null); // 옵셔널 값 꺼내기
    }
}
