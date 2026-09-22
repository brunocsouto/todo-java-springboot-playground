package dev.souto.todo.exception;

import java.util.List;
import java.time.LocalDateTime;

public record ErrorResponse(
    LocalDateTime timestamp,
    Integer status,
    String error,
    List<String> messages
) {
}
