package id.co.evan.project.aggregator.fault;

import id.co.evan.project.aggregator.model.response.ErrorResponse;
import id.co.evan.project.aggregator.util.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(ServerWebExchange exchange) {
        return buildResponse(ErrorCode.GENERAL_ERROR, exchange);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ServerWebExchange exchange) {
        return buildResponse(ErrorCode.RESOURCE_NOT_FOUND, exchange);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ServerWebExchange exchange) {
        return buildResponse(ErrorCode.INVALID_INPUT, exchange);
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSXXX"));

        var errorBody = new ErrorResponse(
            errorCode.getCode(),
            errorCode.getDefaultMessage(),
            timestamp,
            path
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }
}