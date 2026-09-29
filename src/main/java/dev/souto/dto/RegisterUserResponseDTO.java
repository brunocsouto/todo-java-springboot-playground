package dev.souto.dto;

public record RegisterUserResponseDTO(String name, String email) {
    public static RegisterUserResponseDTO of(String name, String email) {
        return new RegisterUserResponseDTO(name, email);
    }
}
