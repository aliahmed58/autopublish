package com.autopublish.socials.controller;

import com.autopublish.socials.entities.LinkedinAccessToken;
import com.autopublish.socials.entities.LinkedinUserInfo;
import com.autopublish.socials.services.LinkedinService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/linkedin")
public class LinkedinController {

    @Autowired
    private LinkedinService linkedinService;

    private static final Logger logger = LoggerFactory.getLogger(LinkedinController.class);

    private final String state = "MUkai1tENrs2AYOnlBoqKPJxnvj1Ag";

    @GetMapping("")
    public String renderHomePage(Model model) throws Exception {
        Set<String> scopes = new HashSet<>(List.of("w_member_social", "profile", "openid", "email"));
        String authUrl = linkedinService.getOauth20Url(scopes, state);

        model.addAttribute("authUrl", authUrl);

        return "linkedin-index.html";
    }

    @GetMapping("/success")
    public String authRedirectUri(@RequestParam("code") String code,
                                  @RequestParam("state") String stateParam, Model model,
                                  HttpSession session) {
        // check if state matches the one we sent to prevent CSRF attack
        if (!stateParam.equals(state)) {
            logger.error("State does not match the one returned in URI - Unsfae to processs");
            return "redirect:/linkedin";
        }

        logger.info("Authorization code: {}", code);
        LinkedinAccessToken accessToken = linkedinService.getAccessToken(code);

        model.addAttribute("token", accessToken);
        session.setAttribute("token", accessToken);

        return "linkedin-index.html";
    }

    @GetMapping("/urn")
    public String fetchUrn(Model model, HttpSession session) {
        LinkedinAccessToken accessToken = (LinkedinAccessToken) session.getAttribute("token");
        if (accessToken == null) {
            logger.error("Access token found null");
            return "redirect:/linkedin";
        }
        LinkedinUserInfo profile = linkedinService.getUserProfile(accessToken);
        model.addAttribute("profile", profile);
        return "linkedin-index.html";
    }

    @ModelAttribute
    public void setToken(Model model, HttpSession session) {
        LinkedinAccessToken accessToken = (LinkedinAccessToken) session.getAttribute("token");
        if (accessToken != null) {
            model.addAttribute("token", accessToken);
        }
    }

}
