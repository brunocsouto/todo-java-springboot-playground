package dev.souto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotEmpty(message = "Name is required") String name,
    @NotEmpty(message = "Email is required") @Email String email,
    @NotEmpty(message = "Password is required")
    @Size(min = 8, max = 32)
    String password
) {
    public static UserRequestDTO of(
        String name,
        String email,
        String password
    ) {
        return new UserRequestDTO(name, email, password);
    }
}
