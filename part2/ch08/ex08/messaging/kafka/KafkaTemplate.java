package tacos.messaging.kafka;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tacos.Order;
import tacos.messaging.OrderMessagingService;

@AllArgsConstructor
@Service
public class KafkaTemplate implements OrderMessagingService {

    private org.springframework.kafka.core.KafkaTemplate<String, Order> kafkaTemplate;

    @Override
    public void sendOrder(Order order) {
        kafkaTemplate.send("tacocloud.orders.topic", order);
        // kafkaTemplate.sendDefault(order); 디폴트값 사용시
    }
}
