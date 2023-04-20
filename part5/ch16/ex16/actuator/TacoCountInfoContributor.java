package tacos.actuator;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import tacos.data.TacoRepository;

import java.util.HashMap;

@AllArgsConstructor
@Component
public class TacoCountInfoContributor implements InfoContributor {

    private TacoRepository tacoRepository;

    @Override
    public void contribute(Info.Builder builder) {
        long tacoCount = tacoRepository.count();
        HashMap<String, Object> tacoMap = new HashMap<>();
        tacoMap.put("count", tacoCount);
        builder.withDetail("taco-stats",tacoMap);
    }
}
