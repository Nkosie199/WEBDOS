package com.webdocs.controller;

import com.webdocs.dto.ForgotPasswordRequest;
import com.webdocs.dto.OtpRequest;
import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification endpoints proxied to Mynger API")
public class NotificationController {

    private final MyngerService myngerService;

    public NotificationController(MyngerService myngerService) {
        this.myngerService = myngerService;
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP to email")
    public ResponseEntity<Object> sendOtp(
            @RequestBody OtpRequest body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.sendOtp(body, authHeader).block());
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP")
    public ResponseEntity<Object> verifyOtp(
            @RequestBody OtpRequest body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.verifyOtp(body, authHeader).block());
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send forgot password email")
    public ResponseEntity<Object> forgotPassword(
            @RequestBody ForgotPasswordRequest body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.forgotPassword(body, authHeader).block());
    }

    @PostMapping("/send-email")
    @Operation(summary = "Send email via Mynger")
    public ResponseEntity<Object> sendEmail(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.sendEmail(body, authHeader).block());
    }
}
