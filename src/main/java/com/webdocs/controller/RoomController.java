package com.webdocs.controller;

import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "Room/channel management proxied to Mynger API")
public class RoomController {

    private final MyngerService myngerService;

    public RoomController(MyngerService myngerService) {
        this.myngerService = myngerService;
    }

    @GetMapping
    @Operation(summary = "List all rooms")
    public ResponseEntity<Object> getRooms(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.getRooms(authHeader).block());
    }

    @PostMapping
    @Operation(summary = "Create a room")
    public ResponseEntity<Object> createRoom(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.createRoom(body, authHeader).block());
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "Get a room by ID")
    public ResponseEntity<Object> getRoom(
            @PathVariable String roomId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.getRoom(roomId, authHeader).block());
    }

    @PatchMapping("/{roomId}")
    @Operation(summary = "Update a room")
    public ResponseEntity<Object> updateRoom(
            @PathVariable String roomId,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.updateRoom(roomId, body, authHeader).block());
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "Delete a room")
    public ResponseEntity<Object> deleteRoom(
            @PathVariable String roomId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.deleteRoom(roomId, authHeader).block());
    }
}
