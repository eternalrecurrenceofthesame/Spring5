package tacos.messaging.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import tacos.Order;
import tacos.kitchen.KitchenUI;

@AllArgsConstructor
@Component
@Slf4j
public class OrderListener {

    private KitchenUI ui;


    @KafkaListener(topics="tacocloud.orders.topic")
    public void handle(Order order, Message<Order> message){
        MessageHeaders headers = message.getHeaders();
        log.info("Received from partition {} with timestamp{}",
                headers.get(KafkaHeaders.RECEIVED_PARTITION_ID),
                headers.get(KafkaHeaders.RECEIVED_TIMESTAMP));

        ui.displayOrder(order);
    }

    /*
    @KafkaListener(topics = "tacocloud.orders.topic")
    public void handle(Order order, ConsumerRecord<String, Order> record){
        log.info("Received from partition {] with timestamp {}", record.partition(), record.timestamp());
        ui.displayOrder(order);
    }

     */

}
