package tacocloud.tacocloudemail;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
public class Ingredient {

    private String code;
    private String name;
}
