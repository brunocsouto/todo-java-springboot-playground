package dev.souto.todo.dto;

import dev.souto.todo.domain.CategoryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(
    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Category name must have at most 255 characters")
    String name
) {
    public static CategoryEntity toEntity(CategoryRequestDTO dto) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.name());
        return entity;
    }
}
