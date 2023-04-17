package springreactor.tacos;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private LocalDate placedAt = LocalDate.now();

    @Field("customer") // customer 열을 문서에 저장한다. 
    private User user;

    private List<Taco> tacos = new ArrayList<>();

    public void addDesign(Taco design){
        this.tacos.add(design);
    }
}
