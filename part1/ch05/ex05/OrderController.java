package tacos.web;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tacos.Order;
import tacos.User;
import tacos.data.OrderRepository;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders")
@SessionAttributes("order")
@Controller
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderProps props;


    @GetMapping
    public String orderForUser(
            @AuthenticationPrincipal User user,
            Model model){

        Pageable pageable = PageRequest.of(0, props.getPageSize());
        model.addAttribute("orders",
                orderRepository.findByUserOrderByPlacedAtDesc(user,pageable));

        return "orderList";
    }

    @GetMapping("/current")
    public String orderForm(@AuthenticationPrincipal User user,
                            @ModelAttribute Order order,
                            Model model){
        if(order.getDeliveryName() == null){
            order.setDeliveryName(user.getFullname());
        }
        if(order.getDeliveryStreet() == null){
            order.setDeliveryStreet(user.getStreet());
        }
        if(order.getDeliveryCity() == null){
            order.setDeliveryCity(user.getCity());
        }
        if(order.getDeliveryState() == null){
            order.setDeliveryState(user.getState());
        }
        if(order.getDeliveryZip() == null){
            order.setDeliveryZip(user.getZip());
        }

        model.addAttribute("order", order);

        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid @ModelAttribute Order order,
                               BindingResult bindingResult,
                               SessionStatus sessionStatus){
        if(bindingResult.hasErrors()){
            return "orderForm";
        }

        orderRepository.save(order);
        /**
         * 주문 객체가 데이터베이스에 저장되면 더 이상 세션을 보관할 필요가 없다.
         * 제거하지 않으면 이전 주문 및 연관된 타코가 세션에 남아 있어서 이전 주문이 포함된다.
         */
        sessionStatus.setComplete();

        return "redirect:/";
    }
}
