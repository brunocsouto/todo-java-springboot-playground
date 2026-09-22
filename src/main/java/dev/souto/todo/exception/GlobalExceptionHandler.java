package dev.souto.todo.exception;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
        MethodArgumentNotValidException ex
    ) {
        List<String> messages = ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .toList();

        return response(HttpStatus.BAD_REQUEST, "Validation error", messages);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(
        HttpMessageNotReadableException ex
    ) {
        return response(
            HttpStatus.BAD_REQUEST,
            "Malformed JSON",
            List.of("Request body is not valid JSON")
        );
    }

    @ExceptionHandler({
        ConstraintViolationException.class,
        MethodArgumentTypeMismatchException.class,
    })
    public ResponseEntity<ErrorResponse> handleParameterValidation(
        Exception ex
    ) {
        return response(
            HttpStatus.BAD_REQUEST,
            "Validation error",
            List.of("Request parameter is invalid")
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex
    ) {
        return response(
            HttpStatus.NOT_FOUND,
            "Resource not found",
            List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return response(
            HttpStatus.CONFLICT,
            "Conflict",
            List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(BusinessRulesException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRulesException(
        BusinessRulesException ex
    ) {
        return response(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "Business rule error",
            List.of(ex.getMessage())
        );
    }

    private ResponseEntity<ErrorResponse> response(
        HttpStatus status,
        String error,
        List<String> messages
    ) {
        return ResponseEntity.status(status).body(
            new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                messages
            )
        );
    }
}
