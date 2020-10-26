package com.zenjob.challenge.dto;

import java.util.List;

import lombok.Data;

/**
 * This class is wrapper for the errors
 */
@Data
public class Errors {
    private List<Error> errors;
}
