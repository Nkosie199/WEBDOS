package com.webdocs.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Hidden
public class DashboardController {

    @Value("${spring.application.name:WebDocs}")
    private String appName;

    @Value("${webdocs.mynger-api-url}")
    private String myngerUrl;

    @Value("${webdocs.deepdiary-api-url}")
    private String deepdiaryUrl;

    @GetMapping("/")
    public String dashboard(Model model, HttpSession session) {
        String authToken = (String) session.getAttribute("authToken");
        String currentUser = (String) session.getAttribute("currentUser");

        if (authToken == null) {
            return "redirect:/login";
        }

        model.addAttribute("appName", "WebDocs");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("swaggerUrl", "/swagger-ui.html");
        model.addAttribute("myngerUrl", myngerUrl);
        model.addAttribute("deepdiaryUrl", deepdiaryUrl);
        model.addAttribute("currentUser", currentUser != null ? currentUser : "User");
        model.addAttribute("authToken", authToken);
        return "index";
    }
}
