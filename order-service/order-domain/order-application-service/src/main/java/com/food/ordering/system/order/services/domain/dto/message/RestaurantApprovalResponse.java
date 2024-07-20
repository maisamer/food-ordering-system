package com.food.ordering.system.order.services.domain.dto.message;

import com.food.ordering.system.domain.valueobject.OrderApproveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalResponse {
    private String id;
    private String sagaId;
    private String orderId;
    private String restaurantId;
    private Instant createdAt;
    private OrderApproveStatus orderApproveStatus;
    private final List<String> failureMessages;
}
