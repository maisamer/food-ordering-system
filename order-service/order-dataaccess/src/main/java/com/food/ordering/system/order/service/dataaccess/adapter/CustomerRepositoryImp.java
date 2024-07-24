package com.food.ordering.system.order.service.dataaccess.adapter;

import com.food.ordering.system.order.service.dataaccess.mapper.CustomerDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.repository.CustomerJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.services.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class CustomerRepositoryImp implements CustomerRepository {
    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;
    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId).map(customerDataAccessMapper::customerEntityToCustomer);
    }
}
