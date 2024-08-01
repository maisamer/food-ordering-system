package com.food.ordering.system.order.services.domain;

import com.food.ordering.system.domain.event.EmptyEvent;
import com.food.ordering.system.order.service.domain.OrderDomainService;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.services.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.services.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.saga.SagaStep;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;


    @Override
    public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approved order with id:{}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is approved",order.getId().getValue().toString());
        return EmptyEvent.INSTANT;
    }

    @Override
    public OrderCancelledEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Cancelling order with id:{}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order,
                restaurantApprovalResponse.getFailureMessages(),
                orderCancelledPaymentRequestMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is cancelling",order.getId().getValue().toString());
        return orderCancelledEvent;
    }
}
