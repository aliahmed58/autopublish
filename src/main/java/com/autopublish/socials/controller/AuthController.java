package com.autopublish.socials.controller;

import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ForkJoinPool;

// small endpoint to add new users
/*
  curl -i -X POST -H "Content-Type: application/json" \
  -d '{ "username": "ali", "password": "root", "name": "Ali Ahmed" }' \
  -k https://localhost/register
*/
@RestController
public class AuthController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Customer customer) {
        customerService.save(customer);
        return ResponseEntity.ok("User added");
    }


}
