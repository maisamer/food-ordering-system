package com.food.ordering.system.order.services.domain;

import com.food.ordering.system.order.services.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.services.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.services.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.services.domain.dto.track.TrackOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderTrackCommandHandler {
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return null;
    }}
