package com.webdocs.controller;

import io.swagger.v3.oas.annotations.Hidden;
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
    public String dashboard(Model model) {
        model.addAttribute("appName", "WebDocs");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("swaggerUrl", "/swagger-ui.html");
        model.addAttribute("myngerUrl", myngerUrl);
        model.addAttribute("deepdiaryUrl", deepdiaryUrl);
        return "index";
    }
}
