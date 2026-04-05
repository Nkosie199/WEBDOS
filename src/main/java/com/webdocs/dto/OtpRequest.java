package com.webdocs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "OTP request")
public class OtpRequest {
    @Schema(example = "john@example.com")
    private String email;
    @Schema(example = "123456")
    private String otp;

    public OtpRequest() {}
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
