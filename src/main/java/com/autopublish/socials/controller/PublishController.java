package com.autopublish.socials.controller;

import com.autopublish.socials.entities.ConfigDto;
import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.entities.FBPage;
import com.autopublish.socials.services.CustomerService;
import com.autopublish.socials.services.FBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class PublishController {

    @Autowired
    private FBService fbService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/publish")
    public String publishPage() {
        return "publish";
    }

    @PostMapping("/update-config")
    public String updateConfig(@ModelAttribute ConfigDto config, Model model) {
        for (FBPage page : config.getPages()) {
            fbService.setPublishEnabled(page.getPageId(), page.isPublishEnabled());
        }
        return "publish";
    }

    @ModelAttribute
    public void modelAttributes(Model model, Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Customer customer = customerService.getByUsername(userDetails.getUsername());
        assert customer != null;

        ConfigDto configDto = new ConfigDto();
        configDto.addPages(customer.getPages());

        model.addAttribute("user", customer);
        model.addAttribute("pages", configDto);
    }
}
