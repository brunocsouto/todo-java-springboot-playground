package dev.souto.dto;

public record LoginResponseDTO(String token) {
    public static LoginResponseDTO of(String token) {
        return new LoginResponseDTO(token);
    }
}
