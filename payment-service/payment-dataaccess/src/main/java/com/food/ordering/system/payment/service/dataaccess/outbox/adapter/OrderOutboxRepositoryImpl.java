package com.food.ordering.system.payment.service.dataaccess.outbox.adapter;

import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.dataaccess.outbox.exception.OrderOutboxNotFoundException;
import com.food.ordering.system.payment.service.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper;
import com.food.ordering.system.payment.service.dataaccess.outbox.repository.OrderOutboxMessageJpaRepository;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.repository.OrderOutboxMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderOutboxRepositoryImpl implements OrderOutboxMessageRepository {

    private final OrderOutboxMessageJpaRepository orderOutboxJpaRepository;
    private final OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;


    @Override
    public OrderOutboxMessage save(OrderOutboxMessage orderPaymentOutboxMessage) {
        return orderOutboxDataAccessMapper
                .orderOutboxEntityToOrderOutboxMessage(orderOutboxJpaRepository
                        .save(orderOutboxDataAccessMapper
                                .orderOutboxMessageToOutboxEntity(orderPaymentOutboxMessage)));
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        return Optional.of(orderOutboxJpaRepository.findByTypeAndOutboxStatus(sagaType, outboxStatus)
                .orElseThrow(() -> new OrderOutboxNotFoundException("Approval outbox object " +
                        "cannot be found for saga type " + sagaType))
                .stream()
                .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String sagaType,
                                                                            UUID sagaId,
                                                                            PaymentStatus paymentStatus,
                                                                            OutboxStatus outboxStatus) {
        return orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(sagaType, sagaId,
                        paymentStatus, outboxStatus)
                .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(sagaType, outboxStatus);
    }
}
