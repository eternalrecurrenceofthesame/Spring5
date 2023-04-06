package tacos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class Taco {

    @NotNull
    @Size(min =5, message="이름은 5 글자보다 작아야 합니다.")
    private String name;
    @Size(min =1, message="최소 하나 이상의 성분을 선택해야 합니당.")
    private List<String> ingredients;
}
