package com.food.ordering.system.order.service.application.controller;

import com.food.ordering.system.order.services.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.services.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.services.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.services.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.services.domain.ports.input.service.OrderApplicationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/orders",produces = "application/vnd.api.v1+json")
public class OrderController {
    private final OrderApplicationService orderApplicationService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand){
        log.info("Creating order for customer: {} for restaurant: {}", createOrderCommand.getCustomerId(),
                createOrderCommand.getRestaurantId());
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        log.info("Order created with trackingId:{}", createOrderResponse.getOrderTrackingId());
        return ResponseEntity.ok(createOrderResponse);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID trackingId){
        TrackOrderResponse trackOrder  = orderApplicationService.trackOrder(
                TrackOrderQuery.builder().orderTrackingId(trackingId).build());
        log.info("Return order status with trackingId:{}", trackingId);
        return ResponseEntity.ok(trackOrder);
    }
}
