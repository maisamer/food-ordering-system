package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class OrderKafkaMessageHelper {
    public <T> CompletableFuture<SendResult<String, T>>
    getKafkaCallback(String responseTopicName, T requestAvroModel,String orderId,String requestAvroModelName) {
        return new CompletableFuture<SendResult<String, T>>().handle((result, throwable) -> {
                    if (throwable == null) {
                        // On success
                        RecordMetadata recordMetadata = result.getRecordMetadata();
                        log.info("Received successful response from Kafka for order id: {} " +
                                        "topic: {} partition: {} offset: {} timestamp: {}",
                                orderId,
                                recordMetadata.topic(),
                                recordMetadata.partition(),
                                recordMetadata.offset(),
                                recordMetadata.timestamp());
                    } else {
                        // On failure
                        log.error("Error while sending "+ requestAvroModelName + " message: {} to topic: {}",
                                requestAvroModel.toString(), responseTopicName, throwable);
                    }
                    return result;
                }

        );
    }
}
