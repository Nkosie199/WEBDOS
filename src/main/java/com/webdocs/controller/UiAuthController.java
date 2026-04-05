package com.webdocs.controller;

import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@Hidden
public class UiAuthController {

    private final MyngerService myngerService;

    public UiAuthController(MyngerService myngerService) {
        this.myngerService = myngerService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("authToken") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        try {
            Map<String, Object> body = Map.of("email", email, "password", password);
            Object response = myngerService.signIn(body, null).block();

            String token = null;
            String username = email;

            if (response instanceof Map<?, ?> responseMap) {
                Object tokenObj = responseMap.get("token");
                if (tokenObj == null) tokenObj = responseMap.get("accessToken");
                if (tokenObj == null) tokenObj = responseMap.get("access_token");
                if (tokenObj != null) token = tokenObj.toString();

                Object userObj = responseMap.get("username");
                if (userObj == null) userObj = responseMap.get("email");
                if (userObj != null) username = userObj.toString();
            }

            if (token == null) {
                model.addAttribute("error", "Invalid credentials. Please try again.");
                return "login";
            }

            session.setAttribute("authToken", "Bearer " + token);
            session.setAttribute("currentUser", username);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect(HttpSession session) {
        if (session.getAttribute("authToken") != null) {
            return "redirect:/";
        }
        return "redirect:/login";
    }
}
