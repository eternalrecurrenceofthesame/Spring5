package tacos.messaging.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import tacos.Order;

@AllArgsConstructor
@Component
public class RabbitOrderReceiver {

    private RabbitTemplate rabbit;
    private MessageConverter converter;


    public Order receiveOrder(){
        return rabbit.receiveAndConvert("tacocloud.order.queue",
                new ParameterizedTypeReference<Order>() {});
    }

    /*
    public Order receiveOrder(){
        Message message = rabbit.receive("tacocloud.order.queue", 30000);

        return message != null ? (Order) converter.fromMessage(message) : null;
    }

     */
}
