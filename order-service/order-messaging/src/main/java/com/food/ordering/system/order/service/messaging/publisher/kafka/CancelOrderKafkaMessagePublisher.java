package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.order.services.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.services.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;
    @Override
    public void publish(OrderCancelledEvent domainEvent) {

        String orderId = domainEvent.getOrder().getId().getValue().toString();

        try {
            log.info("Received OrderCancelledEvent for order id: {}", orderId);

            PaymentRequestAvroModel paymentRequestAvroModel =
                    orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(domainEvent);
            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel,
                    orderKafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getPaymentRequestTopicName()
                            , paymentRequestAvroModel,orderId,"PaymentRequestAvroModel"));

            log.info("PaymentRequestAvroModel send to kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message for order id: {}, error: {}",
                    orderId , e.getMessage());
        }
    }
}
