package com.autopublish.socials.controller;

import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.entities.Entry;
import com.autopublish.socials.entities.FBPage;
import com.autopublish.socials.services.CustomerService;
import com.autopublish.socials.services.EntryService;
import com.autopublish.socials.services.FBService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {

    Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private EntryService entryService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private FBService fbService;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${facebook.appSecret}")
    private String appSecret;

    @Value("${facebook.appId}")
    private String appId;

    @GetMapping("/")
    public String renderIndex(Model model) throws JsonProcessingException, InterruptedException {
        return "index";
    }

    @PostMapping("/fb-login")
    public String getResponse(@RequestBody String requestBody, Model model) throws JsonProcessingException, InterruptedException {
        Customer customer = (Customer) model.getAttribute("user");
        fbService.getUserLongAccessToken(requestBody, customer.getUsername());
        return "index";
    }

    // Methods for showing entries
    @PostMapping("/newEntry")
    public String newEntry(Model model,@Validated Entry entry) {
        model.addAttribute("entry", new Entry());
        entryService.save(entry);
        return "redirect:/";
    }

    // attributes needed
    @ModelAttribute
    public void allEntries(Model model) {
        model.addAttribute("entries", entryService.findAll());
    }

    @ModelAttribute
    public void currentUser(Model model, Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        // TODO: handle null case
        Customer customer = customerService.getByUsername(userDetails.getUsername());
        Entry entry = new Entry();
        entry.setCustomerId(customer);
        model.addAttribute("user", customer);
        model.addAttribute("entry", entry);

        // see if tokens already exist?
        boolean userLongLivedTokenExists = customer.getFbUserAccessToken() != null;
        model.addAttribute("userTokenStatus", userLongLivedTokenExists);
    }

    @GetMapping("/pages")
    public String getCustomerPages(Model model) throws JsonProcessingException {
        Customer customer = (Customer) model.getAttribute("user");
        assert customer != null;
        model.addAttribute("pages", fbService.getPagesByCustomer(customer));
        return "index";
    }


}
