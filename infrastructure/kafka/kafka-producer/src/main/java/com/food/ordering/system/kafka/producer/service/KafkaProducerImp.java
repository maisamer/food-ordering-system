package com.food.ordering.system.kafka.producer.service;

import com.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaProducerImp <K extends Serializable,V extends SpecificRecordBase> implements KafkaProducer<K,V> {
    private final KafkaTemplate<K,V> kafkaTemplate;
    @Override
    public void send(String topicName, K key, V message, CompletableFuture<SendResult<K, V>> callback) {
        log.info("Sending message={} to topic={}", message, topicName);
        try {
            CompletableFuture<SendResult<K, V>> completableFuture = kafkaTemplate.send(topicName, key, message);
            completableFuture
                    .thenAccept(result -> {
                        log.info("Sent message={} with offset={}", message, result.getRecordMetadata().offset());
                        callback.complete(result);
                    })
                    .exceptionally(ex -> {
                        log.error("Error on kafka producer with key: {}, message: {} and exception: {}", key, message, ex.getMessage());
                        callback.completeExceptionally(new KafkaProducerException("Error on kafka producer with key: " + key + " and message: " + message, ex));
                        return null;
                    });
        } catch (KafkaProducerException ex) {
            log.error("Error on kafka producer with key: {}, message: {} and exception: {}", key, message, ex.getMessage());
            throw new KafkaProducerException("Error on kafka producer with key: " + key + " and message: " + message, ex);
        }
    }

    @PreDestroy
    public void close(){
        if(kafkaTemplate != null) {
            log.info("Closing kafka producer");
            kafkaTemplate.destroy();
        }
    }
}
