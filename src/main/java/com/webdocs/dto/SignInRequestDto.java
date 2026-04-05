package com.webdocs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sign-in request payload")
public class SignInRequestDto {
    @Schema(description = "Username or email", example = "john@example.com")
    private String username;
    @Schema(description = "Password", example = "secret123")
    private String password;

    public SignInRequestDto() {}
    public SignInRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
