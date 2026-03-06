package com.algaworks.algashop.billing.presentation.exception;

import com.algaworks.algashop.billing.domain.model.exception.DomainException;

public class GatewayTimeoutException extends DomainException {

    private GatewayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void throwGatewayTimeout(String message, Throwable cause) {
        throw new GatewayTimeoutException(message, cause);
    }

}
