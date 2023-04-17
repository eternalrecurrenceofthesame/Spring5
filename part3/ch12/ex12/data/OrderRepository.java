package springreactor.tacos.data;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import springreactor.tacos.Order;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
}
