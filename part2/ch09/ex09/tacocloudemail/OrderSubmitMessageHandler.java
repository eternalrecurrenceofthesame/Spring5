package tacocloud.tacocloudemail;

import lombok.AllArgsConstructor;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@Component
public class OrderSubmitMessageHandler implements GenericHandler<Order> {


    private RestTemplate rest;
    private ApiProperties apiProperties;


    @Override
    public Object handle(Order order, MessageHeaders headers) {
        rest.postForObject(apiProperties.getUrl(), order, String.class);

        return null;
    }
}
