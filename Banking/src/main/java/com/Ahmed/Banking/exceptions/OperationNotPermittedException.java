package com.Ahmed.Banking.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OperationNotPermittedException extends RuntimeException {

    private final String errorMsg;

    private final String operationId;

    private final String source;

}
