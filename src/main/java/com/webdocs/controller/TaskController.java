package com.webdocs.controller;

import com.webdocs.service.DeepDiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management proxied to DeepDiary API")
public class TaskController {

    private final DeepDiaryService deepDiaryService;

    public TaskController(DeepDiaryService deepDiaryService) {
        this.deepDiaryService = deepDiaryService;
    }

    @GetMapping
    @Operation(summary = "List all tasks")
    public ResponseEntity<Object> getTasks(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.getTasks(authHeader).block());
    }

    @PostMapping
    @Operation(summary = "Create a task")
    public ResponseEntity<Object> createTask(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.createTask(body, authHeader).block());
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<Object> getTaskById(
            @PathVariable String taskId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.getTaskById(taskId, authHeader).block());
    }

    @PatchMapping("/{taskId}")
    @Operation(summary = "Update a task")
    public ResponseEntity<Object> updateTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.updateTask(taskId, body, authHeader).block());
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Object> deleteTask(
            @PathVariable String taskId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.deleteTask(taskId, authHeader).block());
    }
}
