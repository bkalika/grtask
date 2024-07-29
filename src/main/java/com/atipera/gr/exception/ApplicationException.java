package com.atipera.gr.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/28/2024
 */
@ToString
@Getter
public class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    private final String message;

    public ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
