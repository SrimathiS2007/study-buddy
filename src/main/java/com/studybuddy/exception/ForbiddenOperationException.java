package com.studybuddy.exception;

/** -> HTTP 403 */
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
