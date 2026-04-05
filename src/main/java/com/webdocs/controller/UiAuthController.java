package com.webdocs.controller;

import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

        String token = null;

        // ── Step 1: Try Cognito signin ─────────────────────────────────────
        try {
            Map<String, Object> body = Map.of("username", username, "password", password);
            Object response = myngerService.signIn(body, null).block();

            if (response instanceof Map<?, ?> responseMap) {
                for (String field : new String[]{"token", "accessToken", "access_token", "idToken"}) {
                    Object t = responseMap.get(field);
                    if (t instanceof Map<?, ?> nested) {
                        Object at = nested.get("AccessToken");
                        if (at == null) at = nested.get("IdToken");
                        if (at != null) { token = at.toString(); break; }
                    } else if (t != null) {
                        token = t.toString();
                        break;
                    }
                }
            }
        } catch (WebClientResponseException e) {
            // 400/401/403 from Mynger — Cognito account likely unconfirmed.
            // Fall through to stream token fallback.
        } catch (Exception e) {
            // Network error etc — also fall through
        }

        // ── Step 2: Stream token fallback ─────────────────────────────────
        // If Cognito signin failed but user exists in DB, use stream token.
        // This covers accounts registered but not yet Cognito-confirmed.
        if (token == null) {
            try {
                Object userRecord = myngerService.getUserByUsername(username, null).block();
                if (userRecord instanceof Map<?, ?> userMap
                        && userMap.containsKey("username")
                        && username.equals(userMap.get("username"))) {

                    Object streamResp = myngerService.getStreamToken(username).block();
                    if (streamResp instanceof Map<?, ?> streamMap) {
                        Object streamToken = streamMap.get("token");
                        if (streamToken != null) {
                            token = streamToken.toString();
                        }
                    }
                }
            } catch (Exception fallbackEx) {
                // User doesn't exist or stream token unavailable
            }
        }

        // ── Result ─────────────────────────────────────────────────────────
        if (token == null) {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }

        session.setAttribute("authToken", "Bearer " + token);
        session.setAttribute("currentUser", username);
        return "redirect:/";
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
