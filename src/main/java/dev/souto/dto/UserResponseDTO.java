package dev.souto.dto;

import dev.souto.entity.User;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    boolean isActive
) {
    public static UserResponseDTO of(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.isActive()
        );
    }
}
