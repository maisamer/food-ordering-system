package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.food.ordering.system.domain.DomainConstant.UTC;

@Slf4j
public class OrderDomainServiceImp implements OrderDomainService{
    @Override
    public OrderCreatedEvent validateAndInitializeOrder(Order order, Restaurant restaurant, DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order,restaurant);
        order.initializeOrder();
        order.validateOrder();
        log.info("order with id: {} is initialize",order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)),orderCreatedEventDomainEventPublisher);
    }

    @Override
    public OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher) {
        order.pay();
        log.info("order with id: {} is payed",order.getId().getValue());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)),orderPaidEventDomainEventPublisher);
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("order with id: {} is approved",order.getId().getValue());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages, DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher) {
        order.initCancel(failureMessages);
        log.info("order payment is cancelling for order id: {}",order.getId().getValue());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)),orderCancelledEventDomainEventPublisher);
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("order with id: {} is cancelled",order.getId().getValue());
    }

    private void validateRestaurant(Restaurant restaurant) {
        if(!restaurant.isActive()){
            throw new OrderDomainException("Restaurant with id "+ restaurant.getId().getValue()
                + " is currently not active!");
        }
    }

    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        order.getItems().stream().forEach(item->{
            restaurant.getProducts().stream().forEach(product -> {
                Product currentProduct = item.getProduct();
                if(product.equals(currentProduct)){
                    currentProduct.updateWithConfirmedNameAndPrice(product.getName(),product.getPrice());
                }
            });
        });
    }
}
