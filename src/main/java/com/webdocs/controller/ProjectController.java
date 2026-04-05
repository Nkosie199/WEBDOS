package com.webdocs.controller;

import com.webdocs.service.DeepDiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Project management proxied to DeepDiary API")
public class ProjectController {

    private final DeepDiaryService deepDiaryService;

    public ProjectController(DeepDiaryService deepDiaryService) {
        this.deepDiaryService = deepDiaryService;
    }

    @GetMapping
    @Operation(summary = "List all projects")
    public ResponseEntity<Object> getProjects(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.getProjects(authHeader).block());
    }

    @PostMapping
    @Operation(summary = "Create a project")
    public ResponseEntity<Object> createProject(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.createProject(body, authHeader).block());
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<Object> getProjectById(
            @PathVariable String projectId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.getProjectById(projectId, authHeader).block());
    }

    @PatchMapping("/{projectId}")
    @Operation(summary = "Update a project")
    public ResponseEntity<Object> updateProject(
            @PathVariable String projectId,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.updateProject(projectId, body, authHeader).block());
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Object> deleteProject(
            @PathVariable String projectId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.deleteProject(projectId, authHeader).block());
    }
}
