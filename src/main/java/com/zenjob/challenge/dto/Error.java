package com.zenjob.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class is used to store error messages
 */
@Data
@AllArgsConstructor
public class Error {
    private String message;
}
