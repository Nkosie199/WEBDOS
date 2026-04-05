package com.webdocs.controller;

import com.webdocs.service.DeepDiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/time")
@Tag(name = "Time", description = "Time tracking proxied to DeepDiary API")
public class TimeController {

    private final DeepDiaryService deepDiaryService;

    public TimeController(DeepDiaryService deepDiaryService) {
        this.deepDiaryService = deepDiaryService;
    }

    @GetMapping
    @Operation(summary = "Get all time entries")
    public ResponseEntity<Object> getTime(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.getTime(authHeader).block());
    }

    @PostMapping
    @Operation(summary = "Create a time entry")
    public ResponseEntity<Object> createTime(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(deepDiaryService.createTime(body, authHeader).block());
    }
}
