package com.webdocs.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Hidden
public class MoneyUiController {

    @GetMapping("/money")
    public String money(Model model, HttpSession session) {
        if (session.getAttribute("authToken") == null) return "redirect:/login";
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        model.addAttribute("authToken", session.getAttribute("authToken"));
        return "money";
    }
}
