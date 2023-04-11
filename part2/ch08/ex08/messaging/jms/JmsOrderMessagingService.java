package tacos.messaging.jms;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import tacos.Order;
import tacos.messaging.OrderMessagingService;

@AllArgsConstructor
@Service
public class JmsOrderMessagingService implements OrderMessagingService {

    private JmsTemplate jms;

    // convertAndSend
    @Override
    public void sendOrder(Order order) {
       jms.convertAndSend("tacocloud.order.queue", order,
               message -> {
           message.setStringProperty("X_ORDER_SOURCE", "WEB");
           return message;
          });
    }

    /*
    @Override
    public void sendOrder(Order order) {

        jms.send("tacocloud.order.queue",new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(order);

                //jms.send(session -> session.createObjectMessage(order)); 간단한 람다식 표현
            }
        });
    }

     */

}
