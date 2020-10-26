package com.zenjob.challenge.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.zenjob.challenge.dto.Error;
import com.zenjob.challenge.dto.Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to handle exceptions from the controllers
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    Errors errors = new Errors();

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Errors handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException e) {
        logger.error(e.getMessage());
        List<Error> errorList = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            Error inputError = new Error(error.getDefaultMessage());
            errorList.add(inputError);
        });
        errors.setErrors(errorList);
        return errors;
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Errors handleInvalidFormatExceptions(InvalidFormatException e) {
        logger.error(e.getMessage());
        List<Error> errorList = new ArrayList<>();
        Error error = new Error("value '" + e.getValue().toString() + "' is of Invalid Format");
        errorList.add(error);
        errors.setErrors(errorList);
        return errors;
    }

    @ExceptionHandler(RestApplicationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Errors handleRestApplicationExceptions(RestApplicationException e) {
        logger.error(e.getMessage());
        List<Error> errorList = new ArrayList<>();
        Error error = new Error(e.getMessage());
        errorList.add(error);
        errors.setErrors(errorList);
        return errors;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Errors handleConstraintViolationExceptions(ConstraintViolationException e) {
        logger.error(e.getMessage());
        List<Error> errorList = new ArrayList<>();
        Error error = new Error(e.getMessage());
        errorList.add(error);
        errors.setErrors(errorList);
        return errors;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Errors handleMethodArgumentTypeMismatchExceptions(MethodArgumentTypeMismatchException e) {
        logger.error(e.getMessage());
        List<Error> errorList = new ArrayList<>();
        Error error = new Error("value '" + e.getValue() + "' provided is Invalid");
        errorList.add(error);
        errors.setErrors(errorList);
        return errors;
    }

}
