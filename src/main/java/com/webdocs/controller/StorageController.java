package com.webdocs.controller;

import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Hidden
public class StorageController {

    private final MyngerService myngerService;

    public StorageController(MyngerService myngerService) {
        this.myngerService = myngerService;
    }

    // --- UI ---

    @GetMapping("/files")
    public String filesPage(Model model, HttpSession session) {
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        model.addAttribute("authToken", session.getAttribute("authToken"));
        return "files";
    }

    // --- API proxies ---

    @GetMapping("/api/storage/files")
    @ResponseBody
    public ResponseEntity<Object> listFiles(@RequestParam(required = false) String username,
                                             HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String user = username != null ? username : (String) session.getAttribute("currentUser");
        Object result = myngerService.listFiles(user != null ? user : "", auth).block();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/storage/files/{fileId}")
    @ResponseBody
    public ResponseEntity<Object> deleteFile(@PathVariable String fileId, HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        Object result = myngerService.deleteFile(fileId, auth).block();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/storage/files/{fileId}/download")
    @ResponseBody
    public ResponseEntity<Object> getDownloadUrl(@PathVariable String fileId, HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        Object result = myngerService.getDownloadUrl(fileId, auth).block();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/storage/stats")
    @ResponseBody
    public ResponseEntity<Object> getStats(@RequestParam(required = false) String username,
                                            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String user = username != null ? username : (String) session.getAttribute("currentUser");
        Object result = myngerService.getStorageStats(user != null ? user : "", auth).block();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/storage/presigned-url")
    @ResponseBody
    public ResponseEntity<Object> getPresignedUrl(@RequestBody(required = false) Object body,
                                                   HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        Object result = myngerService.getPresignedUploadUrl(body, auth).block();
        return ResponseEntity.ok(result);
    }
}
