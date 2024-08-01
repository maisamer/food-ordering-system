package com.food.ordering.system.order.services.domain;

import com.food.ordering.system.domain.event.EmptyEvent;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.services.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.services.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGES_DELIMITER;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class RestaurantApprovalResponseMessageListenerImp implements RestaurantApprovalResponseMessageListener {
    private final OrderApprovalSaga orderApprovalSaga;
    @Override
    public void orderApprove(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.process(restaurantApprovalResponse);
        log.info("Order is approved for order id: {}", restaurantApprovalResponse.getOrderId());
    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
        OrderCancelledEvent orderCancelledEvent = orderApprovalSaga.rollback(restaurantApprovalResponse);
        log.info("Publishing OrderCancelledEvent for order id: {} with failure messages: {}",
                restaurantApprovalResponse.getOrderId(),
                String.join(FAILURE_MESSAGES_DELIMITER, restaurantApprovalResponse.getFailureMessages()));
        orderCancelledEvent.fire();
    }
}
