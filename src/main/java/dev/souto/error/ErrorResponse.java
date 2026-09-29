package dev.souto.error;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    LocalDateTime timestamp,
    Integer status,
    String error,
    List<String> messages
) {}
