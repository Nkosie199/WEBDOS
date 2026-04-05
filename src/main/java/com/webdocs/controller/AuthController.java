package com.webdocs.controller;

import com.webdocs.dto.ConfirmSignUpDto;
import com.webdocs.dto.SignInRequestDto;
import com.webdocs.dto.SignUpRequestDto;
import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication endpoints proxied to Mynger API")
public class AuthController {

    private final MyngerService myngerService;

    public AuthController(MyngerService myngerService) {
        this.myngerService = myngerService;
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign in to Mynger")
    public ResponseEntity<Object> signIn(
            @RequestBody SignInRequestDto request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.signIn(request, authHeader).block());
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new Mynger account")
    public ResponseEntity<Object> signUp(
            @RequestBody SignUpRequestDto request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.signUp(request, authHeader).block());
    }

    @PostMapping("/confirm-signup")
    @Operation(summary = "Confirm Mynger account registration")
    public ResponseEntity<Object> confirmSignUp(
            @RequestBody ConfirmSignUpDto request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.confirmSignUp(request, authHeader).block());
    }
}
