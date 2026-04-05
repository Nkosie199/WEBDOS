package com.webdocs.service;

import com.webdocs.sitegenerator.DirectoryMapper;
import com.webdocs.sitegenerator.PageCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SiteGeneratorService {

    @Value("${webdocs.output-dir}")
    private String outputDir;

    /**
     * Generate a static site from the given directory path and return it as a ZIP byte array.
     *
     * @param directoryPath absolute path to the source directory
     * @return ZIP bytes of the generated MyStaticSite folder
     */
    public byte[] generateSite(String directoryPath) {
        if (directoryPath == null || directoryPath.isBlank()) {
            throw new IllegalArgumentException("Directory path must not be blank");
        }

        Path sourcePath = Paths.get(directoryPath);
        if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        try {
            // Walk the file tree
            DirectoryMapper directoryMapper = new DirectoryMapper();
            Files.walkFileTree(sourcePath, directoryMapper);

            // Create pages
            PageCreator creator = new PageCreator(directoryPath, directoryMapper.getAllDirectories());
            creator.createByDirectories();
            creator.createAllContentPage(directoryMapper.getContentList());

            // Zip the generated site folder
            Path siteFolderPath = creator.getSitePagesFolderPath();
            return zipDirectory(siteFolderPath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate site: " + e.getMessage(), e);
        }
    }

    /**
     * Return a list of available template names from the classpath sitegenerator resources.
     */
    public List<String> getAvailableTemplates() {
        return List.of("template.html", "template_page.html", "template_directory.html", "template_home.html");
    }

    /**
     * Preview a page by applying the given content map to the specified template.
     */
    public String previewPage(String templateName, java.util.Map<String, String> contentMap) {
        if (templateName == null || templateName.isBlank()) {
            throw new IllegalArgumentException("Template name must not be blank");
        }
        com.webdocs.sitegenerator.IOManager ioManager = new com.webdocs.sitegenerator.IOManager();
        StringBuffer template = ioManager.getTextResource(templateName);
        if (template.isEmpty()) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }
        String html = template.toString();
        if (contentMap != null) {
            for (java.util.Map.Entry<String, String> entry : contentMap.entrySet()) {
                html = html.replace(entry.getKey(), entry.getValue());
            }
        }
        return html;
    }

    private byte[] zipDirectory(Path directory) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = directory.getParent().relativize(file);
                    zos.putNextEntry(new ZipEntry(relativePath.toString().replace("\\", "/")));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = directory.getParent().relativize(dir);
                    String entryName = relativePath.toString().replace("\\", "/");
                    if (!entryName.isEmpty()) {
                        zos.putNextEntry(new ZipEntry(entryName + "/"));
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return baos.toByteArray();
    }
}
