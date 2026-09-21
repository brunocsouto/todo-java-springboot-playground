package dev.souto.todo.dto;

import dev.souto.todo.entity.FolderEntity;

public record FolderResponseDTO(
    String name
) {
    public static FolderResponseDTO toDto(FolderEntity folder) {
        return new FolderResponseDTO(folder.getName());
    }
}
