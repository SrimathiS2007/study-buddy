package com.studybuddy.exception;

/** -> HTTP 409 (duplicate / business-rule conflict) */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
