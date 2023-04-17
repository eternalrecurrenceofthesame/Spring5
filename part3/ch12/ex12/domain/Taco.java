package springreactor.tacos;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;


@Data
@RestResource(rel="tacos", path="tacos")
@Document
public class Taco {

    @Id
    private String id;

    @NotNull
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;

    private LocalDate createdAt = LocalDate.now();

    @Size(min=1, message="You must choose at least 1 ingredient")
    private List<Ingredient> ingredients;
}
