package com.webdocs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to generate a static site from a directory path")
public class GenerateSiteRequest {
    @Schema(description = "Absolute path to the directory to generate a site from", example = "/home/user/photos")
    private String path;

    public GenerateSiteRequest() {}
    public GenerateSiteRequest(String path) { this.path = path; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
