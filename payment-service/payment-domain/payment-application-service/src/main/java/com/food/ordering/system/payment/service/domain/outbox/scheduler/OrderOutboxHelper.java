package com.food.ordering.system.payment.service.domain.outbox.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.exception.PaymentDomainException;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.repository.OrderOutboxMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstant.UTC;
import static com.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
@AllArgsConstructor
public class OrderOutboxHelper {

    private final OrderOutboxMessageRepository orderOutboxMessageRepository;
    private final ObjectMapper objectMapper;
    

    @Transactional(readOnly = true)
    public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(UUID sagaId,
                                                                                               PaymentStatus
                                                                                                       paymentStatus) {
        return orderOutboxMessageRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(ORDER_SAGA_NAME, sagaId,
                paymentStatus, OutboxStatus.COMPLETED);
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderOutboxMessage>> getOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
        return orderOutboxMessageRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
    }

    @Transactional
    public void deleteOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
        orderOutboxMessageRepository.deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
    }

    @Transactional
    public void saveOrderOutboxMessage(OrderEventPayload orderEventPayload,
                                       PaymentStatus paymentStatus,
                                       OutboxStatus outboxStatus,
                                       UUID sagaId) {
        save(OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(orderEventPayload.getCreatedAt())
                .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderEventPayload))
                .paymentStatus(paymentStatus)
                .outboxStatus(outboxStatus)
                .build());
    }

    @Transactional
    public void updateOutboxMessage(OrderOutboxMessage orderOutboxMessage, OutboxStatus outboxStatus) {
        orderOutboxMessage.setOutboxStatus(outboxStatus);
        save(orderOutboxMessage);
        log.info("updateOutboxMessage() Order outbox table status is updated as: {}", outboxStatus.name());
    }

    private String createPayload(OrderEventPayload orderEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderEventPayload);
        } catch (JsonProcessingException e) {
            log.error("createPayload() Could not create OrderEventPayload json!", e);
            throw new PaymentDomainException("Could not create OrderEventPayload json!", e);
        }
    }

    private void save(OrderOutboxMessage orderOutboxMessage) {
        OrderOutboxMessage response = orderOutboxMessageRepository.save(orderOutboxMessage);
        if (response == null) {
            log.error("save() Could not save OrderOutboxMessage!");
            throw new PaymentDomainException("Could not save OrderOutboxMessage!");
        }
        log.info("save() OrderOutboxMessage is saved with id: {}", orderOutboxMessage.getId());
    }
}
