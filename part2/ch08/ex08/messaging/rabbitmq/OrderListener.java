package tacos.messaging.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.stereotype.Component;
import tacos.Order;
import tacos.kitchen.KitchenUI;

@AllArgsConstructor
@Component
public class OrderListener {

    private KitchenUI ui;

    @RabbitListener(queues = "tacocloud.order.queue")
    public void receiverOrder(Order order){
        ui.displayOrder(order);
    }

}
