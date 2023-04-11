package tacos.messaging.jms;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import tacos.Order;

import javax.jms.JMSException;

@AllArgsConstructor
@Component
public class JmsOrderReceiver implements OrderReceiver{


    private JmsTemplate jms;
    private MessageConverter converter;

    @Override
    public Order receiveOrder() throws JMSException {
        /*
        Message message = jms.receive("tacocloud.order.queue");
        return (Order) converter.fromMessage(message);
         */

        return (Order) jms.receiveAndConvert("tacocloud.order.queue");
    }

}
