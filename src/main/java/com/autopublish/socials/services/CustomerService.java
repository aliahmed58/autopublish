package com.autopublish.socials.services;

import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void save(Customer customer) {
        if (customerRepository.findById(customer.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        // hash password
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
    }

    public void update(Customer customer) {
        if (customerRepository.findById(customer.getUsername()).isPresent()) {
            customerRepository.save(customer);
        }
        else {
            throw new RuntimeException("User does not exist");
        }
    }

    public Customer getByUsername(String username) {
        Optional<Customer> customer = customerRepository.findById(username);
        return customer.orElse(null);
    }

    public Customer getReferenceById(String username) {
        return customerRepository.getReferenceById(username);
    }
}

