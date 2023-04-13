package tacocloud.tacocloudemail;

import lombok.Data;

import java.util.List;

import static org.aspectj.util.LangUtil.split;

@Data
public class Taco {

    private final String name;
    private List<String> ingredients;


}
