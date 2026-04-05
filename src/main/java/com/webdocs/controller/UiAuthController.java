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
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        try {
            // Step 1: Try Cognito signin via Mynger API
            Map<String, Object> body = Map.of("username", username, "password", password);
            Object response = myngerService.signIn(body, null).block();

            String token = null;
            String resolvedUsername = username;

            if (response instanceof Map<?, ?> responseMap) {
                // Extract JWT from common field names
                for (String field : new String[]{"token", "accessToken", "access_token", "idToken", "AuthenticationResult"}) {
                    Object t = responseMap.get(field);
                    if (t instanceof Map<?, ?> nested) {
                        // Cognito AuthenticationResult contains AccessToken
                        Object at = nested.get("AccessToken");
                        if (at == null) at = nested.get("IdToken");
                        if (at != null) { token = at.toString(); break; }
                    } else if (t != null) {
                        token = t.toString();
                        break;
                    }
                }
                Object userObj = responseMap.get("username");
                if (userObj == null) userObj = responseMap.get("email");
                if (userObj != null) resolvedUsername = userObj.toString();
            }

            // Step 2: If Cognito signin failed, try stream token fallback
            // (works when account is in DB but Cognito confirmation is pending)
            if (token == null) {
                try {
                    Object userRecord = myngerService.getUserByUsername(username, null).block();
                    if (userRecord instanceof Map<?, ?> userMap && userMap.containsKey("username")) {
                        // User exists in DB — get stream token as auth token
                        Object streamResp = myngerService.getStreamToken(username).block();
                        if (streamResp instanceof Map<?, ?> streamMap) {
                            Object streamToken = streamMap.get("token");
                            if (streamToken != null) {
                                token = streamToken.toString();
                                resolvedUsername = username;
                            }
                        }
                    }
                } catch (Exception fallbackEx) {
                    // User doesn't exist at all
                }
            }

            if (token == null) {
                model.addAttribute("error", "Invalid username or password.");
                return "login";
            }

            session.setAttribute("authToken", "Bearer " + token);
            session.setAttribute("currentUser", resolvedUsername);
            return "redirect:/";

        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("301")) {
                msg = "API configuration error — please contact support.";
            } else if (msg != null && msg.length() > 150) {
                msg = msg.substring(0, 150) + "...";
            }
            model.addAttribute("error", "Login failed: " + msg);
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
