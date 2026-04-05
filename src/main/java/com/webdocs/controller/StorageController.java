package com.webdocs.controller;

import com.webdocs.service.MyngerService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    // ─── UI Page ──────────────────────────────────────────────────────────────

    @GetMapping("/files")
    public String filesPage(Model model, HttpSession session) {
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        model.addAttribute("authToken", session.getAttribute("authToken"));
        return "files";
    }

    // ─── Storage API Proxies (real Mynger endpoints) ──────────────────────────

    /** List all files for the logged-in user → GET /api/storage/files/{userId} */
    @GetMapping("/api/storage/files")
    @ResponseBody
    public ResponseEntity<Object> listFiles(
            @RequestParam(required = false) String userId,
            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String uid = resolve(userId, session);
        return ResponseEntity.ok(myngerService.listFiles(uid, auth).block());
    }

    /** Recent files → GET /api/storage/recent/{userId}?limit=N */
    @GetMapping("/api/storage/recent")
    @ResponseBody
    public ResponseEntity<Object> recentFiles(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "5") int limit,
            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String uid = resolve(userId, session);
        return ResponseEntity.ok(myngerService.getRecentFiles(uid, limit, auth).block());
    }

    /** Full dashboard data → GET /api/storage/dashboard/{userId} */
    @GetMapping("/api/storage/dashboard")
    @ResponseBody
    public ResponseEntity<Object> dashboardData(
            @RequestParam(required = false) String userId,
            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String uid = resolve(userId, session);
        return ResponseEntity.ok(myngerService.getDashboardData(uid, auth).block());
    }

    /** File count → GET /api/storage/count/{userId} */
    @GetMapping("/api/storage/count")
    @ResponseBody
    public ResponseEntity<Object> fileCount(
            @RequestParam(required = false) String userId,
            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String uid = resolve(userId, session);
        return ResponseEntity.ok(myngerService.getFileCount(uid, auth).block());
    }

    /** Total used size (MB) → GET /api/storage/size/{userId} */
    @GetMapping("/api/storage/size")
    @ResponseBody
    public ResponseEntity<Object> totalSize(
            @RequestParam(required = false) String userId,
            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String uid = resolve(userId, session);
        return ResponseEntity.ok(myngerService.getTotalSize(uid, auth).block());
    }

    /** Storage limit (MB) → GET /api/storage/limit/{userId} */
    @GetMapping("/api/storage/limit")
    @ResponseBody
    public ResponseEntity<Object> storageLimit(
            @RequestParam(required = false) String userId,
            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String uid = resolve(userId, session);
        return ResponseEntity.ok(myngerService.getStorageLimit(uid, auth).block());
    }

    /** Analytics (uploads per day) → GET /api/storage/analytics/{userId} */
    @GetMapping("/api/storage/analytics")
    @ResponseBody
    public ResponseEntity<Object> analytics(
            @RequestParam(required = false) String userId,
            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String uid = resolve(userId, session);
        return ResponseEntity.ok(myngerService.getAnalytics(uid, auth).block());
    }

    /** File types breakdown → GET /api/storage/types/{userId} */
    @GetMapping("/api/storage/types")
    @ResponseBody
    public ResponseEntity<Object> fileTypes(
            @RequestParam(required = false) String userId,
            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        String uid = resolve(userId, session);
        return ResponseEntity.ok(myngerService.getFileTypes(uid, auth).block());
    }

    /** Get presigned upload URL → POST /api/files/presigned-url */
    @PostMapping("/api/storage/presigned-url")
    @ResponseBody
    public ResponseEntity<Object> getPresignedUrl(
            @RequestBody(required = false) Object body,
            HttpSession session) {
        String auth = (String) session.getAttribute("authToken");
        return ResponseEntity.ok(myngerService.getPresignedUploadUrl(body, auth).block());
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private String resolve(String paramUserId, HttpSession session) {
        if (paramUserId != null && !paramUserId.isBlank()) return paramUserId;
        Object uid = session.getAttribute("currentUser");
        return uid != null ? uid.toString() : "";
    }
}
