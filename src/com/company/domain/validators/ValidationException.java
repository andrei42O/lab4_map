package com.company.domain.validators;

public class ValidationException extends RuntimeException {
    public ValidationException() {
    }

    /**
     * The function creates an error with a certain message
     * @param message - String
     */
    public ValidationException(String message) {
        super(message);
    }
    /**
     * The function creates an error with a certain message and cause
     * @param message - String
     * @param cause - Throwable
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * The function creates an error with a certain cause
     * @param cause - Throwable
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

    /**
     * The function creates an error with a certain message, cause and lets to be suppression to be enabled and
     * if it can be written in the Stack Trace
     * @param message - String
     * @param cause - Throwable
     * @param enableSuppression - boolean
     * @param writableStackTrace - boolean
     */
    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
