package com.autopublish.socials.auth;

import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private CustomerService customerService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerService.getByUsername(username);
        if (customer == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        UserDetails userDetails = User.withUsername(customer.getUsername())
                .password(customer.getPassword())
                .build();

        return userDetails;
    }
}
