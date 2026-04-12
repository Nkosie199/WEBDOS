package com.webdocs.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class SpreadsheetImportService {

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd MMM yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("M/d/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
    );

    public List<Map<String, Object>> parseSpreadsheet(MultipartFile file, String username) throws Exception {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";

        if (filename.endsWith(".ods") || contentType.contains("opendocument.spreadsheet")) {
            return parseOds(file.getInputStream(), username);
        } else if (filename.endsWith(".xlsx") || contentType.contains("spreadsheetml")) {
            return parseXlsx(file.getInputStream(), false, username);
        } else if (filename.endsWith(".xls") || contentType.contains("ms-excel")) {
            return parseXlsx(file.getInputStream(), true, username);
        } else if (filename.endsWith(".csv") || contentType.contains("csv")) {
            return parseCsv(new String(file.getBytes()), username);
        } else {
            byte[] bytes = file.getBytes();
            try {
                return parseXlsx(new ByteArrayInputStream(bytes), false, username);
            } catch (Exception e1) {
                try {
                    return parseOds(new ByteArrayInputStream(bytes), username);
                } catch (Exception e2) {
                    try {
                        return parseCsv(new String(bytes), username);
                    } catch (Exception e3) {
                        throw new IllegalArgumentException(
                                "Could not parse file. Supported formats: ODS, XLSX, XLS, CSV. " + e1.getMessage());
                    }
                }
            }
        }
    }

    private List<Map<String, Object>> parseXlsx(InputStream is, boolean legacy, String username) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Workbook wb = legacy ? new HSSFWorkbook(is) : new XSSFWorkbook(is)) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                results.addAll(parsePoiSheet(sheet, username));
            }
        }
        return results;
    }

    private List<Map<String, Object>> parsePoiSheet(Sheet sheet, String username) {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Integer> headers = null;
        DataFormatter formatter = new DataFormatter();

        for (Row row : sheet) {
            if (row == null) continue;
            List<String> cells = new ArrayList<>();
            for (int c = 0; c <= row.getLastCellNum(); c++) {
                Cell cell = row.getCell(c);
                cells.add(cell != null ? formatter.formatCellValue(cell).trim() : "");
            }
            if (headers == null) {
                Map<String, Integer> detected = detectHeaders(cells);
                if (!detected.isEmpty()) {
                    headers = detected;
                }
                continue;
            }
            Map<String, Object> entry = mapRowToEntry(cells, headers, username);
            if (entry != null) rows.add(entry);
        }
        return rows;
    }

    private List<Map<String, Object>> parseOds(InputStream is, String username) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        byte[] bytes = is.readAllBytes();

        try (java.util.zip.ZipInputStream zip = new java.util.zip.ZipInputStream(new ByteArrayInputStream(bytes))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if ("content.xml".equals(entry.getName())) {
                    byte[] xmlBytes = zip.readAllBytes();
                    results.addAll(parseOdsXml(new ByteArrayInputStream(xmlBytes), username));
                    break;
                }
            }
        }
        return results;
    }

    private List<Map<String, Object>> parseOdsXml(InputStream is, String username) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        org.w3c.dom.Document doc = dbf.newDocumentBuilder().parse(is);

        String TABLE_NS = "urn:oasis:names:tc:opendocument:xmlns:table:1.0";
        String TEXT_NS = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
        String OFFICE_NS = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";

        org.w3c.dom.NodeList tables = doc.getElementsByTagNameNS(TABLE_NS, "table");
        for (int t = 0; t < tables.getLength(); t++) {
            org.w3c.dom.Element table = (org.w3c.dom.Element) tables.item(t);
            org.w3c.dom.NodeList rowNodes = table.getElementsByTagNameNS(TABLE_NS, "table-row");
            Map<String, Integer> headers = null;

            for (int r = 0; r < rowNodes.getLength(); r++) {
                org.w3c.dom.Element rowEl = (org.w3c.dom.Element) rowNodes.item(r);
                org.w3c.dom.NodeList cellNodes = rowEl.getElementsByTagNameNS(TABLE_NS, "table-cell");
                List<String> cells = new ArrayList<>();

                for (int c = 0; c < cellNodes.getLength(); c++) {
                    org.w3c.dom.Element cell = (org.w3c.dom.Element) cellNodes.item(c);
                    String text = "";
                    String dateVal = cell.getAttributeNS(OFFICE_NS, "date-value");
                    if (!dateVal.isBlank()) {
                        text = dateVal;
                    } else {
                        org.w3c.dom.NodeList ps = cell.getElementsByTagNameNS(TEXT_NS, "p");
                        StringBuilder sb = new StringBuilder();
                        for (int p = 0; p < ps.getLength(); p++) {
                            sb.append(ps.item(p).getTextContent());
                        }
                        text = sb.toString().trim();
                    }
                    int repeat = 1;
                    String repeatStr = cell.getAttributeNS(TABLE_NS, "number-columns-repeated");
                    if (!repeatStr.isBlank()) {
                        try {
                            repeat = Math.min(Integer.parseInt(repeatStr), 20);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    for (int rep = 0; rep < repeat; rep++) cells.add(text);
                }

                // Trim trailing empties
                while (!cells.isEmpty() && cells.get(cells.size() - 1).isBlank())
                    cells.remove(cells.size() - 1);
                if (cells.isEmpty()) continue;

                if (headers == null) {
                    Map<String, Integer> detected = detectHeaders(cells);
                    if (!detected.isEmpty()) headers = detected;
                    continue;
                }
                Map<String, Object> rowEntry = mapRowToEntry(cells, headers, username);
                if (rowEntry != null) results.add(rowEntry);
            }
        }
        return results;
    }

    private List<Map<String, Object>> parseCsv(String content, String username) {
        List<Map<String, Object>> results = new ArrayList<>();
        String[] lines = content.split("\n");
        Map<String, Integer> headers = null;
        for (String line : lines) {
            List<String> cells = Arrays.asList(line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1));
            cells = cells.stream().map(s -> s.trim().replaceAll("^\"|\"$", "")).toList();
            if (headers == null) {
                Map<String, Integer> detected = detectHeaders(cells);
                if (!detected.isEmpty()) {
                    headers = detected;
                    continue;
                }
            }
            if (headers == null) continue;
            Map<String, Object> entry = mapRowToEntry(cells, headers, username);
            if (entry != null) results.add(entry);
        }
        return results;
    }

    private Map<String, Integer> detectHeaders(List<String> cells) {
        Map<String, Integer> found = new HashMap<>();
        for (int i = 0; i < cells.size(); i++) {
            String h = cells.get(i).toLowerCase().trim();
            if (h.contains("date")) found.put("date", i);
            else if (h.contains("desc") || h.contains("item") || h.contains("narration"))
                found.put("description", i);
            else if (h.contains("amount") || h.equals("value") || h.contains("current value"))
                found.put("amount", i);
            else if (h.contains("categor") || h.equals("type")) found.put("category", i);
            else if (h.contains("note") || h.contains("comment") || h.contains("memo"))
                found.put("notes", i);
        }
        return found.size() >= 2 ? found : new HashMap<>();
    }

    private Map<String, Object> mapRowToEntry(List<String> cells, Map<String, Integer> headers, String username) {
        String date = getCell(cells, headers, "date");
        String description = getCell(cells, headers, "description");
        String amountStr = getCell(cells, headers, "amount");
        String category = getCell(cells, headers, "category");
        String notes = getCell(cells, headers, "notes");

        if (date.isBlank() && description.isBlank() && amountStr.isBlank()) return null;
        if (description.isBlank()) return null;

        double amount;
        try {
            amount = Double.parseDouble(amountStr.replaceAll("[^\\d.\\-]", ""));
        } catch (NumberFormatException e) {
            return null;
        }

        String type = amount >= 0 ? "INCOME" : "EXPENSE";
        if (category != null && (category.toLowerCase().contains("expense")
                || category.toLowerCase().contains("rent")
                || category.toLowerCase().contains("food")
                || category.toLowerCase().contains("transport")
                || category.toLowerCase().contains("fee")
                || category.toLowerCase().contains("charge")
                || category.toLowerCase().contains("withdrawal"))) {
            type = "EXPENSE";
        } else if (category != null && (category.toLowerCase().contains("income")
                || category.toLowerCase().contains("salary")
                || category.toLowerCase().contains("deposit")
                || category.toLowerCase().contains("repayment"))) {
            type = "INCOME";
        }

        String parsedDate = parseDate(date);

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("username", username);
        entry.put("date", parsedDate);
        entry.put("description", description);
        entry.put("amount", Math.abs(amount));
        entry.put("category", category != null && !category.isBlank() ? category : "Other");
        entry.put("type", type);
        entry.put("notes", notes != null ? notes : "");
        entry.put("source", "spreadsheet_import");
        return entry;
    }

    private String getCell(List<String> cells, Map<String, Integer> headers, String key) {
        Integer idx = headers.get(key);
        if (idx == null || idx >= cells.size()) return "";
        return cells.get(idx).trim();
    }

    private String parseDate(String raw) {
        if (raw == null || raw.isBlank()) return LocalDate.now().toString();
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(raw, fmt).toString();
            } catch (DateTimeParseException ignored) {
            }
        }
        if (raw.matches("\\d{4}-\\d{2}-\\d{2}.*")) return raw.substring(0, 10);
        return raw;
    }

    public List<Map<String, Object>> mapMyngerTransactions(Object txns, String username) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (!(txns instanceof List)) return result;
        for (Object item : (List<?>) txns) {
            if (!(item instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> t = (Map<String, Object>) item;
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("username", username);
            entry.put("date",
                    t.getOrDefault("createdAt", t.getOrDefault("date", LocalDate.now().toString())));
            entry.put("description",
                    t.getOrDefault("description", t.getOrDefault("type", "Mynger Transaction")));
            Object amt = t.getOrDefault("amount", 0);
            double amount = amt instanceof Number ? ((Number) amt).doubleValue() : 0;
            entry.put("amount", Math.abs(amount));
            entry.put("category", t.getOrDefault("category", "Mynger"));
            entry.put("type", amount >= 0 ? "INCOME" : "EXPENSE");
            entry.put("notes", "Imported from Mynger");
            entry.put("source", "mynger");
            result.add(entry);
        }
        return result;
    }

    public List<Map<String, Object>> mapOpenBankTransactions(Object txns, String username) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (!(txns instanceof List)) return result;
        for (Object item : (List<?>) txns) {
            if (!(item instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> t = (Map<String, Object>) item;
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("username", username);
            entry.put("date",
                    t.getOrDefault("dateTime", t.getOrDefault("createdAt", LocalDate.now().toString())));
            String txType = String.valueOf(t.getOrDefault("transactionType", "UNKNOWN"));
            entry.put("description",
                    txType + (t.containsKey("amount") ? " \u2014 R" + t.get("amount") : ""));
            Object amt = t.getOrDefault("amount", 0);
            double amount = amt instanceof Number ? ((Number) amt).doubleValue() : 0;
            entry.put("amount", amount);
            entry.put("category", txType.equals("DEPOSIT") ? "Income" : "Expense");
            entry.put("type", txType.equals("DEPOSIT") ? "INCOME" : "EXPENSE");
            entry.put("notes", "Imported from OpenBank");
            entry.put("source", "openbank");
            result.add(entry);
        }
        return result;
    }
}
