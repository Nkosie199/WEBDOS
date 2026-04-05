package com.webdocs.controller;

import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Files", description = "File management endpoints proxied to Mynger API")
public class FileController {

    private final MyngerService myngerService;

    public FileController(MyngerService myngerService) {
        this.myngerService = myngerService;
    }

    @PostMapping("/presigned-url")
    @Operation(summary = "Get a presigned URL for file upload")
    public ResponseEntity<Object> getPresignedUrl(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(myngerService.getPresignedUrl(body, authHeader).block());
    }
}
