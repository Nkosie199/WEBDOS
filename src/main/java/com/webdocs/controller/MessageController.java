package com.webdocs.controller;

import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "Messaging endpoints proxied to Mynger API")
public class MessageController {

    private final MyngerService myngerService;

    public MessageController(MyngerService myngerService) {
        this.myngerService = myngerService;
    }

    @GetMapping
    @Operation(summary = "List all messages")
    public ResponseEntity<Object> getMessages(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.getMessages(authHeader).block());
    }

    @PostMapping
    @Operation(summary = "Create a message")
    public ResponseEntity<Object> createMessage(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.createMessage(body, authHeader).block());
    }

    @GetMapping("/{messageId}")
    @Operation(summary = "Get a message by ID")
    public ResponseEntity<Object> getMessage(
            @PathVariable String messageId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.getMessage(messageId, authHeader).block());
    }

    @PatchMapping("/{messageId}")
    @Operation(summary = "Update a message")
    public ResponseEntity<Object> updateMessage(
            @PathVariable String messageId,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.updateMessage(messageId, body, authHeader).block());
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "Delete a message")
    public ResponseEntity<Object> deleteMessage(
            @PathVariable String messageId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.deleteMessage(messageId, authHeader).block());
    }
}
