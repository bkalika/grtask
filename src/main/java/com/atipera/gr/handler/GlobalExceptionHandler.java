package com.atipera.gr.handler;

import com.atipera.gr.dto.ApplicationExceptionDto;
import com.atipera.gr.exception.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/28/2024
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationExceptionDto> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApplicationExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationExceptionDto> handleException(ApplicationException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ApplicationExceptionDto(ex.getStatus().value(), ex.getMessage()));
    }

    public static ExchangeFilterFunction webClientErrorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, errorBody)));
            } else if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new ApplicationException(HttpStatus.BAD_REQUEST, errorBody)));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }
}
