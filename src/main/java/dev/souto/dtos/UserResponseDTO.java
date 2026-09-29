package dev.souto.dtos;

import dev.souto.model.User;

public record UserResponseDTO(
    String id,
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
