package dev.souto.todo.dto;

import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.entity.TodoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record TodoRequestDTO(
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must have at most 255 characters")
    String title,

    @Size(max = 2000, message = "Description must have at most 2000 characters")
    String description,

    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Category name must have at most 255 characters")
    String categoryName,

    @NotBlank(message = "Folder name is required")
    @Size(max = 255, message = "Folder name must have at most 255 characters")
    String folderName
) {
    public static TodoEntity toEntity(TodoRequestDTO dto, CategoryEntity category, FolderEntity folder) {
        TodoEntity todo = new TodoEntity();
        todo.setTitle(dto.title);
        todo.setDescription(dto.description);
        todo.setCategory(category);
        todo.setFolder(folder);
        return todo;
    }
}
