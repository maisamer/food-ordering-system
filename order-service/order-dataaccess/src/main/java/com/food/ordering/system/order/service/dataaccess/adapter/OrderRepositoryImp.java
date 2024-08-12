package com.food.ordering.system.order.service.dataaccess.adapter;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.order.service.dataaccess.mapper.OrderDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.repository.OrderJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class OrderRepositoryImp implements OrderRepository {
    private final OrderJpaRepository orderRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;
    @Override
    public Order save(Order order) {
        return orderDataAccessMapper.orderEntityToOrder(
                orderRepository.save(orderDataAccessMapper.orderToOrderEntity(order)));
    }
    @Override
    public Optional<Order> findById(OrderId orderId) {
        return orderRepository.findById(orderId.getValue()).map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderRepository.findByTrackingId(trackingId.getValue())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }
}
