package tacos;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Taco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate createdAt;

    @NotNull
    @Size(min = 5, message="이름은 5 글자 이상이어야 합니다.")
    private String name;

    @ManyToMany(targetEntity = Ingredient.class)
    @Size(min = 1, message="최소 하나 이상의 성분을 선택해야 합니당.")
    private List<Ingredient> ingredients;

    @PrePersist // 엔티티를 영속성 컨텍스트에서 관리하기 전에 호출된다.
    void createdAt(){
        this.createdAt = LocalDate.now();
    }
}
