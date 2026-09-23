package dev.souto.todo.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(
        GlobalExceptionHandler.class
    );

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationError(
        MethodArgumentNotValidException ex
    ) {
        logger.atWarn()
            .addKeyValue("errorType", ex.getClass().getSimpleName())
            .log("Request validation failed");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation error"
        );

        List<String> messages = ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .toList();

        problemDetail.setProperty("messages", messages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            problemDetail
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMalformedJson(
        HttpMessageNotReadableException ex
    ) {
        logger.atWarn()
            .addKeyValue("errorType", ex.getClass().getSimpleName())
            .log("Request contains malformed JSON");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Malformed JSON"
        );
        problemDetail.setProperty(
            "messages",
            List.of("Request body is not valid JSON")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            problemDetail
        );
    }

    @ExceptionHandler({
        ConstraintViolationException.class,
        MethodArgumentTypeMismatchException.class,
    })
    public ResponseEntity<ProblemDetail> handleParameterValidation(
        Exception ex
    ) {
        logger.atWarn()
            .addKeyValue("errorType", ex.getClass().getSimpleName())
            .log("Request parameter validation failed");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation error"
        );
        problemDetail.setProperty(
            "messages",
            List.of("Request parameter is invalid")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            problemDetail
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
        ResourceNotFoundException ex
    ) {
        logger.atWarn()
            .addKeyValue("errorType", ex.getClass().getSimpleName())
            .log("Requested resource was not found");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            "Resource not found"
        );
        problemDetail.setProperty("messages", List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(ConflictException ex) {
        logger.atWarn()
            .addKeyValue("errorType", ex.getClass().getSimpleName())
            .log("Request conflicted with existing resource");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "Conflict"
        );

        problemDetail.setProperty("messages", List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(BusinessRulesException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRulesException(
        BusinessRulesException ex
    ) {
        logger.atWarn()
            .addKeyValue("errorType", ex.getClass().getSimpleName())
            .log("Request violated a business rule");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "Business rule error"
        );

        problemDetail.setProperty("messages", List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
            problemDetail
        );
    }
}
