package com.algaworks.algashop.billing.presentation.exception;

import com.algaworks.algashop.billing.domain.model.exception.DomainException;

public class BadGatewayException extends DomainException {

    public BadGatewayException() {
    }

    private BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void throwBadGateway(String message, Throwable cause) {
        throw new BadGatewayException(message, cause);
    }

}
