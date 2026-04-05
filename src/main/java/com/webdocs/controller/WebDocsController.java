package com.webdocs.controller;

import com.webdocs.dto.GenerateSiteRequest;
import com.webdocs.dto.HealthResponse;
import com.webdocs.dto.PreviewRequest;
import com.webdocs.service.SiteGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/webdocs")
@Tag(name = "WebDocs", description = "Static site generation and health endpoints")
public class WebDocsController {

    private final SiteGeneratorService siteGeneratorService;

    public WebDocsController(SiteGeneratorService siteGeneratorService) {
        this.siteGeneratorService = siteGeneratorService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate a static site from a directory path and return as ZIP")
    public ResponseEntity<byte[]> generateSite(@RequestBody GenerateSiteRequest request) {
        byte[] zipBytes = siteGeneratorService.generateSite(request.getPath());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"MyStaticSite.zip\"")
                .body(zipBytes);
    }

    @GetMapping("/templates")
    @Operation(summary = "List available HTML templates")
    public ResponseEntity<List<String>> getTemplates() {
        return ResponseEntity.ok(siteGeneratorService.getAvailableTemplates());
    }

    @PostMapping("/preview")
    @Operation(summary = "Preview a page generated from a template and content map")
    public ResponseEntity<String> previewPage(@RequestBody PreviewRequest request) {
        String html = siteGeneratorService.previewPage(request.getTemplateName(), request.getContent());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check with upstream API status")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse response = new HealthResponse("UP", "UNKNOWN", "UNKNOWN", "UP");
        return ResponseEntity.ok(response);
    }
}
