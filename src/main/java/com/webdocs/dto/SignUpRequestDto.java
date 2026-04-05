package com.webdocs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sign-up request payload")
public class SignUpRequestDto {
    @Schema(example = "johndoe")
    private String username;
    @Schema(example = "john@example.com")
    private String email;
    @Schema(example = "secret123")
    private String password;
    @Schema(example = "John")
    private String firstName;
    @Schema(example = "Doe")
    private String lastName;

    public SignUpRequestDto() {}
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
