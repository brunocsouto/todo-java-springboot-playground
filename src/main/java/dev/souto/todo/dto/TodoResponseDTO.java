package dev.souto.todo.dto;

import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;

public record TodoResponseDTO(
    String title,
    String description,
    CategoryResponseDTO category,
    FolderResponseDTO folder
) {
    public static TodoResponseDTO toDto(
        String title,
        String description,
        CategoryEntity category,
        FolderEntity folder
    ) {
        return new TodoResponseDTO(
            title,
            description,
            CategoryResponseDTO.toDto(category),
            FolderResponseDTO.toDto(folder)
        );
    }



}
