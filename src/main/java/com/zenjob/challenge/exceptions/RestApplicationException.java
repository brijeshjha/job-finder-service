package com.zenjob.challenge.exceptions;

/**
 * This class is used to throw exceptions related to Internal server error
 */
public class RestApplicationException extends RuntimeException {

    public RestApplicationException(String message) {
        super(message);
    }

}
