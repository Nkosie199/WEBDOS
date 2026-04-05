package com.webdocs.controller;

import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management proxied to Mynger API")
public class UserController {

    private final MyngerService myngerService;

    public UserController(MyngerService myngerService) {
        this.myngerService = myngerService;
    }

    @GetMapping
    @Operation(summary = "List all users")
    public ResponseEntity<Object> getUsers(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.getUsers(authHeader).block());
    }

    @PostMapping
    @Operation(summary = "Create a user")
    public ResponseEntity<Object> createUser(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.createUser(body, authHeader).block());
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get user by username")
    public ResponseEntity<Object> getUserByUsername(
            @PathVariable String username,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.getUserByUsername(username, authHeader).block());
    }

    @PatchMapping("/{username}")
    @Operation(summary = "Update user by username")
    public ResponseEntity<Object> updateUser(
            @PathVariable String username,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.updateUser(username, body, authHeader).block());
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete user by username")
    public ResponseEntity<Object> deleteUser(
            @PathVariable String username,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.deleteUser(username, authHeader).block());
    }
}
