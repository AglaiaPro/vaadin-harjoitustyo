package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.Customer;
import com.example.harjoitustyo.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final HistoryService historyService;

    public CustomerService(CustomerRepository customerRepository, HistoryService historyService) {
        this.customerRepository = customerRepository;
        this.historyService = historyService;
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer save(Customer customer) {
        boolean created = customer.getId() == null;
        Customer saved = customerRepository.save(customer);
        historyService.record("Customer", saved.getId(), created ? "CREATED" : "UPDATED", saved.getName());
        return saved;
    }

    @Transactional
    public void delete(Customer customer) {
        Long id = customer.getId();
        String summary = customer.getName();
        customerRepository.delete(customer);
        historyService.record("Customer", id, "DELETED", summary);
    }
}
