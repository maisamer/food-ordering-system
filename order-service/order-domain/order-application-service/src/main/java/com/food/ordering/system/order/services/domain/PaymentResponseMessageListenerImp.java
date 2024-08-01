package com.food.ordering.system.order.services.domain;

import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.services.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.services.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGES_DELIMITER;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class PaymentResponseMessageListenerImp implements PaymentResponseMessageListener {

    private final OrderPaymentSaga orderPaymentSaga;

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
        OrderPaidEvent orderPaidEvent = orderPaymentSaga.process(paymentResponse);
        log.info("Publishing orderPaidEvent for order id: {}", paymentResponse.getOrderId());
        orderPaidEvent.fire();
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
        orderPaymentSaga.rollback(paymentResponse);
        log.info("Order is roll backed for order id: {} with failure messages: {}",
                paymentResponse.getOrderId(),
                String.join(FAILURE_MESSAGES_DELIMITER, paymentResponse.getFailureMessages()));
    }
}
