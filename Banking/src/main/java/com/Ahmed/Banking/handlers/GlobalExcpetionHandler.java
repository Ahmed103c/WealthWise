package com.Ahmed.Banking.handlers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.Ahmed.Banking.exceptions.ObjectValidationException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExcpetionHandler {
    @ExceptionHandler(ObjectValidationException.class)
    public ResponseEntity<ExceptionRepresentation> handleExcpetion(ObjectValidationException excpetion)
    {
        ExceptionRepresentation representation = ExceptionRepresentation.builder()
                                               .errorMessage("Object Not Valid Exception")
                                               .errorSource(excpetion.getViolationSource())
                                               .validationErrors(excpetion.getViolations())
                                               .build();
        

        return ResponseEntity                      //Renvoyer un object JSON qui s'appelle EceptionRepresentation 
                .status(HttpStatus.BAD_REQUEST)    //qui renvoie type de l'Exception 
                .body(representation);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionRepresentation> handleException(EntityNotFoundException exception)
    {
        ExceptionRepresentation representation = ExceptionRepresentation.builder()
                                                 .errorMessage(exception.getMessage())
                                                 .build();
        return ResponseEntity
               .status(HttpStatus.NOT_FOUND)
               .body(representation);
    }

}
