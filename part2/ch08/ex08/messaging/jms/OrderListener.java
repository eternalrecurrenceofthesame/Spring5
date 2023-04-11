package tacos.messaging.jms;

import lombok.AllArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tacos.Order;
import tacos.kitchen.KitchenUI;

@AllArgsConstructor
@Component
public class OrderListener {

    private KitchenUI ui;

    @JmsListener(destination ="tacocloud.order.queue")
    public void receiverOrder(Order order){
        ui.displayOrder(order);
    }

}
