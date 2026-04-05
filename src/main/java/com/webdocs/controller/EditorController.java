package com.webdocs.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Controller
@Hidden
public class EditorController {

    @GetMapping("/editor")
    public String editorPage(Model model, HttpSession session) {
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        model.addAttribute("authToken", session.getAttribute("authToken"));
        return "editor";
    }

    @PostMapping("/editor/preview")
    @ResponseBody
    public ResponseEntity<String> preview(@RequestBody Map<String, String> payload) {
        String html = payload.getOrDefault("html", "");
        String css = payload.getOrDefault("css", "");
        String js = payload.getOrDefault("js", "");

        String preview = "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                "<style>" + css + "</style></head><body>" + html +
                "<script>" + js + "</script></body></html>";
        return ResponseEntity.ok(preview);
    }

    @PostMapping("/editor/save")
    @ResponseBody
    public ResponseEntity<Map<String, String>> save(@RequestBody Map<String, String> payload) {
        String content = payload.getOrDefault("content", "");
        String filename = payload.getOrDefault("filename", "untitled.html");

        try {
            Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "webdocs-editor");
            Files.createDirectories(tempDir);
            Path filePath = tempDir.resolve(filename);
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
            return ResponseEntity.ok(Map.of("path", filePath.toString(), "status", "saved"));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage(), "status", "error"));
        }
    }
}
