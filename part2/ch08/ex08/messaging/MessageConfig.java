package tacos.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import tacos.Order;

import java.util.HashMap;

public class MessageConfig {

    //@Bean
    public MappingJackson2MessageConverter messageConverter(){
        MappingJackson2MessageConverter messageConverter
                = new MappingJackson2MessageConverter();

        messageConverter.setTypeIdPropertyName("_typeId");

        HashMap<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("order", Order.class);
        messageConverter.setTypeIdMappings(typeIdMappings);

        return messageConverter;

    }
}
