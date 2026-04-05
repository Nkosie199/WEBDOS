package com.webdocs;

import com.webdocs.service.SiteGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SiteGeneratorServiceTest {

    private SiteGeneratorService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new SiteGeneratorService();
        // inject output-dir via reflection since @Value doesn't work outside Spring context
        var field = SiteGeneratorService.class.getDeclaredField("outputDir");
        field.setAccessible(true);
        field.set(service, System.getProperty("java.io.tmpdir") + "/webdocs-test");
    }

    @Test
    void generateSite_returnsNonEmptyZip(@TempDir Path tempDir) throws IOException {
        // Create some test files in the temp directory
        Files.writeString(tempDir.resolve("file1.txt"), "Hello World");
        Files.writeString(tempDir.resolve("file2.txt"), "Another file");
        Path subDir = Files.createDirectory(tempDir.resolve("subfolder"));
        Files.writeString(subDir.resolve("nested.txt"), "Nested content");

        byte[] result = service.generateSite(tempDir.toString());

        assertNotNull(result, "Result should not be null");
        assertTrue(result.length > 0, "Result should be non-empty");
    }

    @Test
    void generateSite_throwsIllegalArgumentException_forInvalidPath() {
        assertThrows(IllegalArgumentException.class, () ->
                service.generateSite("/nonexistent/path/that/does/not/exist/ever"));
    }

    @Test
    void generateSite_throwsIllegalArgumentException_forBlankPath() {
        assertThrows(IllegalArgumentException.class, () ->
                service.generateSite("   "));
    }

    @Test
    void generateSite_throwsIllegalArgumentException_forNullPath() {
        assertThrows(IllegalArgumentException.class, () ->
                service.generateSite(null));
    }

    @Test
    void getAvailableTemplates_returnsNonEmpty() {
        var templates = service.getAvailableTemplates();
        assertNotNull(templates);
        assertFalse(templates.isEmpty(), "Template list should not be empty");
        assertTrue(templates.contains("template.html"), "Should include template.html");
    }
}
