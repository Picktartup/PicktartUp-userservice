package com.picktartup.userservice.exception;

public class BusinessLogicException extends RuntimeException {

    private final ExceptionList exceptionList;

    public BusinessLogicException(ExceptionList exceptionList) {
        super(exceptionList.getMessage());
        this.exceptionList = exceptionList;
    }

    public ExceptionList getExceptionList() {
        return exceptionList;
    }
}