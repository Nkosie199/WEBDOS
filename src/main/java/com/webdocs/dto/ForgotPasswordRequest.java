package com.webdocs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Forgot password request")
public class ForgotPasswordRequest {
    @Schema(example = "john@example.com")
    private String email;

    public ForgotPasswordRequest() {}
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
