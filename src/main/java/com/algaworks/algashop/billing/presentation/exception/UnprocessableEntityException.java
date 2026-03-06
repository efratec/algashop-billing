package com.algaworks.algashop.billing.presentation.exception;


import com.algaworks.algashop.billing.domain.model.exception.DomainException;

public class UnprocessableEntityException extends DomainException {

    private UnprocessableEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void throwUnprocessableEntity(String message, Throwable cause) {
        throw new UnprocessableEntityException(message, cause);
    }

}
