package com.Ahmed.Banking.validators;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.Ahmed.Banking.exceptions.ObjectValidationException;

import jakarta.validation.Validator;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

@Component
public class ObjectsValidator<T> {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();


    public void validate(T ObjectToValidate){
        Set<ConstraintViolation<T>> violations = validator.validate(ObjectToValidate);
        if (!violations.isEmpty()) {
            Set<String> errorMessages = violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.toSet());
            
            //to do Raise Exception 
            throw new ObjectValidationException(errorMessages,ObjectToValidate.getClass().getName());
        }
    }

}
