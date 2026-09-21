package dev.souto.todo.dto;

public record TodoResponseDTO(
    String title,
    String description,
    String category,
    String folder
) {
    public static TodoResponseDTO toDto(
        String title,
        String description,
        String category,
        String folder
    ) {
        return new TodoResponseDTO(
            title,
            description,
            category,
            folder
        );
    }
}
