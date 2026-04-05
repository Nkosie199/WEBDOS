package com.webdocs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Generic request body (flexible key-value map)")
public class GenericRequest {
    private Map<String, Object> data;

    public GenericRequest() {}
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
