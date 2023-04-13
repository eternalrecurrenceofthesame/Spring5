package sia5.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.stereotype.Component;

/**
 * 양방향 게이트웨이
 */
@Component
@MessagingGateway(defaultRequestChannel = "inChannel",
                  defaultReplyChannel = "outChannel")
public interface UpperCaseGateway {

    String uppercase(String in);


    /**
     * 자바 DSL 구현 예시
     */
    /*
    @Bean
    public IntegrationFlow upperCaseFlow(){
        return IntegrationFlows.from("inChannel")
                .<String, String> transform(s -> s.toUpperCase())
                .channel("outChannel")
                .get();
    }

     */
}
