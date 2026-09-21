package dev.souto.todo.dto;

import dev.souto.todo.entity.FolderEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FolderRequestDTO(
    @NotBlank(message = "Folder name is required")
    @Size(max = 255, message = "Folder name must have at most 255 characters")
    String name
) {
    public static FolderEntity toEntity(FolderRequestDTO dto) {
        FolderEntity folder = new FolderEntity();
        folder.setName(dto.name());
        return folder;
    }
}
