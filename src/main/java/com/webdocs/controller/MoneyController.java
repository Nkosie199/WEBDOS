package com.webdocs.controller;

import com.webdocs.service.DeepDiaryService;
import com.webdocs.service.MyngerService;
import com.webdocs.service.OpenBankService;
import com.webdocs.service.SpreadsheetImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/money")
@Tag(name = "Money", description = "Money diary — view, edit, import from Mynger/OpenBank/spreadsheet")
public class MoneyController {

    private final DeepDiaryService deepDiaryService;
    private final MyngerService myngerService;
    private final OpenBankService openBankService;
    private final SpreadsheetImportService spreadsheetImportService;

    public MoneyController(DeepDiaryService deepDiaryService,
                           MyngerService myngerService,
                           OpenBankService openBankService,
                           SpreadsheetImportService spreadsheetImportService) {
        this.deepDiaryService = deepDiaryService;
        this.myngerService = myngerService;
        this.openBankService = openBankService;
        this.spreadsheetImportService = spreadsheetImportService;
    }

    @GetMapping
    @Operation(summary = "Get money diary entries for a user")
    public ResponseEntity<Object> getMoney(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String yearMonth,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpSession session) {
        if (authHeader == null) authHeader = "Bearer " + session.getAttribute("authToken");
        Object result = yearMonth != null
                ? deepDiaryService.getMoneyByMonth(username, yearMonth, authHeader).block()
                : deepDiaryService.getMoney(username, authHeader).block();
        return ResponseEntity.ok(result != null ? result : List.of());
    }

    @PostMapping
    @Operation(summary = "Create a money diary entry")
    public ResponseEntity<Object> createMoney(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpSession session) {
        if (authHeader == null) authHeader = "Bearer " + session.getAttribute("authToken");
        return ResponseEntity.ok(deepDiaryService.createMoneyEntry(body, authHeader).block());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a money diary entry")
    public ResponseEntity<Object> updateMoney(
            @PathVariable String id,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpSession session) {
        if (authHeader == null) authHeader = "Bearer " + session.getAttribute("authToken");
        return ResponseEntity.ok(deepDiaryService.updateMoneyEntry(id, body, authHeader).block());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a money diary entry")
    public ResponseEntity<Object> deleteMoney(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpSession session) {
        if (authHeader == null) authHeader = "Bearer " + session.getAttribute("authToken");
        deepDiaryService.deleteMoneyEntry(id, authHeader).block();
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    @PostMapping(value = "/import/spreadsheet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload spreadsheet and preview parsed money entries (not saved yet)")
    public ResponseEntity<?> importSpreadsheet(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "username", required = false) String username,
            HttpSession session) {
        try {
            if (username == null) username = (String) session.getAttribute("currentUser");
            List<Map<String, Object>> preview = spreadsheetImportService.parseSpreadsheet(file, username);
            return ResponseEntity.ok(Map.of(
                    "preview", preview,
                    "count", preview.size(),
                    "username", username != null ? username : "",
                    "format", "See /api/money/import/format for expected spreadsheet format"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/import/confirm")
    @Operation(summary = "Confirm and save previewed money entries to DeepDiary")
    public ResponseEntity<?> confirmImport(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpSession session) {
        try {
            if (authHeader == null) authHeader = "Bearer " + session.getAttribute("authToken");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> entries = (List<Map<String, Object>>) body.get("entries");
            if (entries == null || entries.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No entries to save"));
            }
            int saved = 0;
            int failed = 0;
            for (Map<String, Object> entry : entries) {
                try {
                    deepDiaryService.createMoneyEntry(entry, authHeader).block();
                    saved++;
                } catch (Exception e) {
                    failed++;
                }
            }
            return ResponseEntity.ok(Map.of("saved", saved, "failed", failed, "total", entries.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/import/mynger")
    @Operation(summary = "Preview Mynger transactions as money entries (for confirmation before saving)")
    public ResponseEntity<?> importFromMynger(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpSession session) {
        try {
            String username = (String) session.getAttribute("currentUser");
            if (authHeader == null) authHeader = "Bearer " + session.getAttribute("authToken");
            Object txns = myngerService.getUserTransactions(username, authHeader).block();
            List<Map<String, Object>> preview = spreadsheetImportService.mapMyngerTransactions(txns, username);
            return ResponseEntity.ok(Map.of("preview", preview, "count", preview.size(), "source", "mynger"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/import/openbank")
    @Operation(summary = "Preview OpenBank transactions as money entries (for confirmation before saving)")
    public ResponseEntity<?> importFromOpenBank(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpSession session) {
        try {
            String username = (String) session.getAttribute("currentUser");
            if (authHeader == null) authHeader = "Bearer " + session.getAttribute("authToken");
            Object txns = openBankService.getTransactionsByUsername(username).block();
            List<Map<String, Object>> preview = spreadsheetImportService.mapOpenBankTransactions(txns, username);
            return ResponseEntity.ok(Map.of("preview", preview, "count", preview.size(), "source", "openbank"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/import/format")
    @Operation(summary = "Get expected spreadsheet format for import")
    public ResponseEntity<?> getImportFormat() {
        return ResponseEntity.ok(Map.of(
                "description",
                "Upload an ODS, XLSX, or CSV file. Each sheet will be scanned for rows matching the expected format.",
                "requiredColumns", List.of("date", "description", "amount", "category"),
                "optionalColumns", List.of("type", "notes"),
                "dateFormats", List.of("YYYY-MM-DD", "DD MMM YYYY", "DD/MM/YYYY", "DD-MM-YYYY"),
                "categoryExamples",
                List.of("Income", "Rent", "Food", "Transport", "Entertainment", "Utilities", "Investment",
                        "Other"),
                "typeValues", List.of("INCOME", "EXPENSE"),
                "amountNote",
                "Use positive values for both income and expenses. Set 'type' to INCOME or EXPENSE to distinguish. If type is absent, positive = INCOME, negative = EXPENSE.",
                "exampleRow", Map.of(
                        "date", "2020-03-20",
                        "description", "Net salary at EasyPay",
                        "amount", "21690.34",
                        "category", "Income",
                        "type", "INCOME",
                        "notes", "March 2020 salary"
                )));
    }
}
