package com.algaworks.algashop.billing.domain.model.creditcard;


import com.algaworks.algashop.billing.domain.model.exception.DomainException;

public class CreditCardNotFoundException extends DomainException {

    private CreditCardNotFoundException() {
    }

    private CreditCardNotFoundException(String message) {
        super(message);
    }

    public static CreditCardNotFoundException of() {
        throw new CreditCardNotFoundException();
    }

    public static void beacuse(String message) {
        throw new CreditCardNotFoundException(message);
    }

}
