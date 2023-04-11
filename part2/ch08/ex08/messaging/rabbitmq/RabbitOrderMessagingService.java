package tacos.messaging.rabbitmq;


import lombok.AllArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Service;
import tacos.Order;
import tacos.messaging.OrderMessagingService;

@AllArgsConstructor
@Service
public class RabbitOrderMessagingService implements OrderMessagingService {

    private RabbitTemplate rabbit;

    /**
     *  convertAndSend
     * */
    @Override
    public void sendOrder(Order order) {
        rabbit.convertAndSend(order, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties props = message.getMessageProperties();
                props.setHeader("X_ORDER_SOURCE", "WEB");
                return message;
            }
        });
    }


    /**
     * send
     */
    /*
    @Override
    public void sendOrder(Order order) {
        MessageConverter converter = rabbit.getMessageConverter();
        MessageProperties props = new MessageProperties(); //spring


        Message message = converter.toMessage(order, props);
        rabbit.send("tacocloud.order","routing_key",  message);
    }

     */
}
