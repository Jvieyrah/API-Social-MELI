package com.meli.social.exception;

public class PostUnprocessableException extends RuntimeException {
    public PostUnprocessableException(String message) {
        super(message);
    }

    public PostUnprocessableException(String message, Throwable cause) {
        super(message, cause);
    }
}