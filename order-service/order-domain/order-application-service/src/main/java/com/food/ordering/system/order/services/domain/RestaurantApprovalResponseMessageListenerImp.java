package com.food.ordering.system.order.services.domain;

import com.food.ordering.system.order.services.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.services.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class RestaurantApprovalResponseMessageListenerImp implements RestaurantApprovalResponseMessageListener {
    @Override
    public void orderApprove(RestaurantApprovalResponse restaurantApprovalResponse) {

    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {

    }
}
