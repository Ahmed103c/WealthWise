package com.Ahmed.Banking.handlers;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY) // si jamais on n'a pas validationsErrors
public class ExceptionRepresentation {
    private String errorMessage;
    private String errorSource;
    private Set<String> validationErrors;
}
