package dev.souto.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TodoUpdateRequestDTO(
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must have at most 255 characters")
    String title,

    @Size(max = 2000, message = "Description must have at most 2000 characters")
    String description,

    String categoryId,

    @NotBlank(message = "Folder id is required")
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "Folder id must be a valid UUID"
    )
    String folderId
) {}
