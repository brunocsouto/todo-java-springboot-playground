package dev.souto.dto;

import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequestDTO(
    @NotEmpty(message = "Name is required") String name,
    @NotEmpty(message = "Email is required") String email,
    @NotEmpty(message = "Password is required") String password
) {
    public static RegisterUserRequestDTO of(
        String name,
        String email,
        String password
    ) {
        return new RegisterUserRequestDTO(name, email, password);
    }
}
