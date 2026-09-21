package dev.souto.todo.dto;

import dev.souto.todo.entity.CategoryEntity;

public record CategoryResponseDTO(
    String name
) {
    public static CategoryResponseDTO toDto(CategoryEntity category) {
        return new CategoryResponseDTO(category.getName());
    }
}
