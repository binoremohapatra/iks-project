package com.example.inventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

/** One consistent JSON error shape for the whole API. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String timestamp, int status, String error, String message, Map<String, String> details) {

    public static ErrorResponse of(HttpStatus status, String message) {
        return of(status, message, null);
    }

    public static ErrorResponse of(HttpStatus status, String message, Map<String, String> details) {
        return new ErrorResponse(Instant.now().toString(), status.value(), status.getReasonPhrase(), message, details);
    }
}
