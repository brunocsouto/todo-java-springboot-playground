package dev.souto.error;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex
    ) {
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleException(
        ResourceNotFoundException ex
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            "Resource not found"
        );

        problemDetail.setProperty("messages", List.of(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleException(ConflictException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "Conflict"
        );

        problemDetail.setProperty("messages", List.of(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMalformedJson(
        HttpMessageNotReadableException ex
    ) {
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

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ProblemDetail> handleBusinessLogicException(
        BusinessLogicException ex
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "Business logic error"
        );
        problemDetail.setProperty("messages", List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
            problemDetail
        );
    }
}
