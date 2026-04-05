package com.webdocs.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Hidden
public class WebErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int status = statusCode != null ? Integer.parseInt(statusCode.toString()) : 500;
        model.addAttribute("status", status);
        model.addAttribute("path", path != null ? path.toString() : "/");

        switch (status) {
            case 403 -> {
                model.addAttribute("error", "Access Denied");
                model.addAttribute("message", "You don't have permission to access this page. Please sign in.");
            }
            case 404 -> {
                model.addAttribute("error", "Page Not Found");
                model.addAttribute("message", "The page you're looking for doesn't exist.");
            }
            case 401 -> {
                model.addAttribute("error", "Unauthorised");
                model.addAttribute("message", "Please sign in to continue.");
            }
            default -> {
                model.addAttribute("error", HttpStatus.valueOf(status).getReasonPhrase());
                model.addAttribute("message", message != null && !message.toString().isBlank()
                        ? message.toString()
                        : "An unexpected error occurred. Please try again.");
            }
        }
        return "error";
    }
}
