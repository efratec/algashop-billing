package com.algaworks.algashop.billing.presentation.exception;

import com.algaworks.algashop.billing.domain.model.exception.DomainException;

public class BadGatewayException extends DomainException {

    public BadGatewayException() {
    }

    public BadGatewayException(String message) {
        super(message);
    }

    public BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void throwBadGateway(String message, Throwable cause) {
        throw new BadGatewayException(message, cause);
    }

    public static class ServerErrorException extends BadGatewayException {
        public ServerErrorException() {
        }

        public ServerErrorException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ClientErrorException extends BadGatewayException {
        public ClientErrorException() {
        }

        public ClientErrorException(String message) {
            super(message);
        }

        public ClientErrorException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
