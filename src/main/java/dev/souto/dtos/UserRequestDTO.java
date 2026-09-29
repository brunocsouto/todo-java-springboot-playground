package dev.souto.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 32) String password
) {
    public static UserRequestDTO of(String name, String email, String password) {
        return new UserRequestDTO(name, email, password);
    }
}
