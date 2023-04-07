package tacos;

import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Taco_Order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate placedAt;

    @NotBlank(message="이름은 필수입니다.")
    private String deliveryName;
    @NotBlank(message="거리는 필수입니다.")
    private String deliveryStreet;
    @NotBlank(message="도시는 필수입니다.")
    private String deliveryCity;
    @NotBlank(message="배송 상태는 필수입니다.")
    private String deliveryState;
    @NotBlank(message="우편 번호는 필수입니다.")
    private String deliveryZip;
    //@CreditCardNumber(message="유효하지 않은 신용카드 번호입니다.") // 룬 알고리즘 검사에 합격한 유효한 신용 카드 번호여야 한다.
    private String ccNumber;
    @Pattern(regexp = "^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
    message="MM/YY 포맷을 사용하시오")
    private String ccExpiration;

    @Digits(integer=3, fraction=0, message="Invalid CVV") // 입력 값이 정확히 3 자리 숫자인지 검사
    private String ccCVV;

    @ManyToMany(targetEntity = Taco.class)
    private List<Taco> tacos = new ArrayList<>();

    public void addDesign(Taco design){
        this.tacos.add(design);
    }

    @PrePersist
    void placedAt(){
        this.placedAt = LocalDate.now();
    }
}
