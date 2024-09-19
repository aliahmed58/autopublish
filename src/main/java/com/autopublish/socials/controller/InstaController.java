package com.autopublish.socials.controller;

import com.autopublish.socials.entities.ConfigDto;
import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.entities.InstaAcc;
import com.autopublish.socials.services.CustomerService;
import com.autopublish.socials.services.InstaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/insta")
public class InstaController {

    private final static Logger logger = LoggerFactory.getLogger(InstaController.class);
    @Autowired
    private CustomerService customerService;

    @Autowired
    private InstaService instaService;

    @GetMapping("/")
    public String index() {
        return "insta-index.html";
    }

    @GetMapping("/success")
    public String successfulAuthorization(@RequestParam("code") String code, Model model) {
        InstaAcc acc = instaService.getUserLongAccessToken(code);
        if (acc == null) {
            logger.error("Failed authentication with instagram");
            return "insta-index.html";
        }
        instaService.testPublish(acc);
        model.addAttribute("instaAcc", acc);
        return "insta-index.html";
    }

    @GetMapping("/publish")
    public String testPublish(Model model) {
        InstaAcc acc = (InstaAcc) model.getAttribute("instaAcc");
        if (acc == null) {
            logger.info("Insta account not connected to test publishing");
            return "insta-index.html";
        }
        return "insta-index.html";
    }

    @ModelAttribute
    public void modelAttributes(Model model, Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Customer customer = customerService.getByUsername(userDetails.getUsername());
        assert customer != null;

        model.addAttribute("user", customer);
    }
}
