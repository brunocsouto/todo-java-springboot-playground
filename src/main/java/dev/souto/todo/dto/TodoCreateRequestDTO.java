package dev.souto.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TodoCreateRequestDTO(
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must have at most 255 characters")
    String title,

    @Size(max = 2000, message = "Description must have at most 2000 characters")
    String description,

    @NotNull(message = "Category id is required") String categoryId,

    @NotNull(message = "Folder id is required") String folderId
) {}
