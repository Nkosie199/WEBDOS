package com.webdocs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Preview request for a page from a template + content map")
public class PreviewRequest {
    @Schema(description = "Template name (e.g. template.html)", example = "template.html")
    private String templateName;
    @Schema(description = "Content map of token replacements")
    private Map<String, String> content;

    public PreviewRequest() {}
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public Map<String, String> getContent() { return content; }
    public void setContent(Map<String, String> content) { this.content = content; }
}
