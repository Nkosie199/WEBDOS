package com.webdocs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Health check response")
public class HealthResponse {
    private String status;
    private String myngerApi;
    private String deepDiaryApi;
    private String siteGenerator;
    private String timestamp;

    public HealthResponse() {
        this.timestamp = Instant.now().toString();
    }

    public HealthResponse(String status, String myngerApi, String deepDiaryApi, String siteGenerator) {
        this();
        this.status = status;
        this.myngerApi = myngerApi;
        this.deepDiaryApi = deepDiaryApi;
        this.siteGenerator = siteGenerator;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMyngerApi() { return myngerApi; }
    public void setMyngerApi(String myngerApi) { this.myngerApi = myngerApi; }
    public String getDeepDiaryApi() { return deepDiaryApi; }
    public void setDeepDiaryApi(String deepDiaryApi) { this.deepDiaryApi = deepDiaryApi; }
    public String getSiteGenerator() { return siteGenerator; }
    public void setSiteGenerator(String siteGenerator) { this.siteGenerator = siteGenerator; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
