package dev.souto.todo.dto;

import dev.souto.todo.entity.CategoryEntity;

public record CategoryResponseDTO(
    String id,
    String name
) {
    public static CategoryResponseDTO toDto(CategoryEntity category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
