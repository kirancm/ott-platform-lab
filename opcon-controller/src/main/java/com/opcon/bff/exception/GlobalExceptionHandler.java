package com.opcon.bff.exception;

import com.opcon.bff.dto.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException exception, ServerWebExchange exchange) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), List.of(), exchange);
    }

    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception exception, ServerWebExchange exchange) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of(), exchange);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(WebExchangeBindException exception, ServerWebExchange exchange) {
        List<String> details = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", details, exchange);
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleDownstream(DownstreamServiceException exception, ServerWebExchange exchange) {
        return build(HttpStatus.BAD_GATEWAY, exception.getMessage(), List.of(), exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception exception, ServerWebExchange exchange) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", List.of(exception.getMessage()), exchange);
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String message,
            List<String> details,
            ServerWebExchange exchange
    ) {
        ApiErrorResponse payload = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .details(details)
                .build();
        return ResponseEntity.status(status).body(payload);
    }
}
