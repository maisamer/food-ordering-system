package com.food.ordering.system.order.services.domain.ports.input.message.listener.restaurantapproval;

import com.food.ordering.system.order.services.domain.dto.message.RestaurantApprovalResponse;

public interface RestaurantApprovalResponseMessageListener {
    void orderApprove(RestaurantApprovalResponse restaurantApprovalResponse);
    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
