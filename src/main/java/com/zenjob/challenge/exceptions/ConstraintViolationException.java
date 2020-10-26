package com.zenjob.challenge.exceptions;

/**
 * This Exception class is used when constraints or objective are not met
 */
public class ConstraintViolationException extends RuntimeException {

    public ConstraintViolationException(String message) {
        super(message);
    }

}
