package app.githubrepolister.exception;

import app.githubrepolister.dto.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<CustomErrorResponse>> handleResponseStatusException(ResponseStatusException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                ex.getStatusCode().value(),
                ex.getReason() != null ? ex.getReason() : "Unexpected error"
        );
        return Mono.just(new ResponseEntity<>(errorResponse, ex.getStatusCode()));
    }

    @ExceptionHandler(WebClientResponseException.Forbidden.class)
    public Mono<ResponseEntity<CustomErrorResponse>> handleWebClientResponseException(WebClientResponseException.Forbidden ex) {
        String responseBody = ex.getResponseBodyAsString();
        String message = "Forbidden: " + ex.getMessage();

        if (responseBody.contains("API rate limit exceeded")) {
            message = "API rate limit exceeded. Please authenticate to get a higher rate limit.";
        }

        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                message
        );

        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN));
    }
}
