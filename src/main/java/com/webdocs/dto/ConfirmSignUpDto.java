package com.webdocs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Confirm sign-up request")
public class ConfirmSignUpDto {
    @Schema(example = "johndoe")
    private String username;
    @Schema(example = "123456")
    private String confirmationCode;

    public ConfirmSignUpDto() {}
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getConfirmationCode() { return confirmationCode; }
    public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }
}
