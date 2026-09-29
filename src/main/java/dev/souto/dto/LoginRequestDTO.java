package dev.souto.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(
    @NotEmpty(message = "Email is required") String email,
    @NotEmpty(message = "Password is required") String password
) {
    public static LoginRequestDTO of(String email, String password) {
        return new LoginRequestDTO(email, password);
    }
}
