package com.food.ordering.system.order.services.domain;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import com.food.ordering.system.order.services.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.services.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.services.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.services.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.services.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.services.domain.ports.output.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class OrderTrackCommandHandler {
    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        Optional<Order> order = orderRepository
                .findByTrackingId(new TrackingId(trackOrderQuery.getOrderTrackingId()));
        if(order.isEmpty()){
            log.warn("Could not find order with tracking id:{}",trackOrderQuery.getOrderTrackingId());
            throw new OrderNotFoundException("Could not find order with tracking id: "+ trackOrderQuery.getOrderTrackingId());
        }
        return orderDataMapper.orderToTrackOrderResponse(order.get());
    }}
