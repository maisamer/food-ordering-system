package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurant.RestaurantApprovalRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.*;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.food.ordering.system")
public class OrderTestConfiguration {
    @Bean
    OrderRepository orderRepository(){
        return Mockito.mock(OrderRepository.class);
    }
    @Bean
    CustomerRepository customerRepository(){
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    RestaurantRepository restaurantRepository(){
        return Mockito.mock(RestaurantRepository.class);
    }
    @Bean
    PaymentOutboxRepository paymentOutboxRepository(){
        return Mockito.mock(PaymentOutboxRepository.class);
    }
    @Bean
    ApprovalOutboxRepository approvalOutboxRepository(){
        return Mockito.mock(ApprovalOutboxRepository.class);
    }
    @Bean
    PaymentRequestMessagePublisher paymentRequestMessagePublisher(){
        return Mockito.mock(PaymentRequestMessagePublisher.class);
    }
    @Bean
    RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher(){
        return Mockito.mock(RestaurantApprovalRequestMessagePublisher.class);
    }
    @Bean
    public OrderDomainService orderDomainService(){
        return new OrderDomainServiceImp();
    }
}
