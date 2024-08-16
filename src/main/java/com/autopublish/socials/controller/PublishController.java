package com.autopublish.socials.controller;

import com.autopublish.socials.entities.*;
import com.autopublish.socials.services.CustomerService;
import com.autopublish.socials.services.EntryService;
import com.autopublish.socials.services.FBService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class PublishController {

    @Autowired
    private FBService fbService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private EntryService entryService;

    @GetMapping("/publish")
    public String publishPage() {
        return "publish";
    }

    @PostMapping("/update-config")
    public String updateConfig(@ModelAttribute ConfigDto config, Model model) {
        Customer customer = (Customer) model.getAttribute("user");
        fbService.setPublishingEnabled(new HashSet<>(config.getPages()), customer);
        return "publish";
    }

    @GetMapping("/publish-entry")
    public String publishEntry(Model model) throws JsonProcessingException {
        Customer customer = (Customer) model.getAttribute("user");
        List<Entry> customerEntries = entryService.findAllByCustomer(customer);
        if (customerEntries.isEmpty()) {
            model.addAttribute("error", "No entries found to publish, please post an entry");
            return "publish";
        }
        fbService.publishPost(customerEntries, customer);
        return "publish";
    }

    @ModelAttribute
    public void modelAttributes(Model model, Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Customer customer = customerService.getByUsername(userDetails.getUsername());
        assert customer != null;

        ConfigDto configDto = new ConfigDto();
        configDto.addPages(customer.getCustomerPages());

        model.addAttribute("user", customer);
        model.addAttribute("pages", configDto);
    }
}
