package com.food.ordering.system.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;
    public <T,U> CompletableFuture<SendResult<String, T>>
    getKafkaCallback(String responseTopicName, T requestAvroModel,
                     U outboxMessage, BiConsumer<U, OutboxStatus> outboxCallback,
                     String orderId, String requestAvroModelName) {
        return new CompletableFuture<SendResult<String, T>>().handle((result, throwable) -> {
                    if (throwable == null) {
                        // On success
                        RecordMetadata recordMetadata = result.getRecordMetadata();
                        log.info("getKafkaCallback() Received successful response from Kafka for order id: {} " +
                                        "topic: {} partition: {} offset: {} timestamp: {}",
                                orderId,
                                recordMetadata.topic(),
                                recordMetadata.partition(),
                                recordMetadata.offset(),
                                recordMetadata.timestamp());
                        outboxCallback.accept(outboxMessage,OutboxStatus.COMPLETED);
                    } else {
                        // On failure
                        log.error("getKafkaCallback() Error while sending {} with message: {} and outbox type: {} to topic: {}",
                                requestAvroModelName,
                                requestAvroModel.toString(),
                                outboxMessage.getClass().getName(),
                                responseTopicName, throwable);
                        outboxCallback.accept(outboxMessage,OutboxStatus.FAILED);
                    }
                    return result;
                }

        );
    }

    public<T>  T getOrderEventPayload(String payload, Class<T> tClass) {
        try {
            return objectMapper.readValue(payload,tClass);
        } catch (JsonProcessingException e) {
            log.error("Could not read {} object!", tClass.getName(), e);
            throw new OrderDomainException("Could not read " + tClass.getName() + " object!", e);
        }
    }
}
