package io.osoon.web.exception;

import org.springframework.http.HttpStatus;

/**
 * @author whiteship
 */
public class ApiError {

    HttpStatus status;

    String message;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
