package com.algaworks.algashop.billing.domain.model.creditcard;


import com.algaworks.algashop.billing.domain.model.exception.DomainEntityNotFoundException;

public class CreditCardNotFoundException extends DomainEntityNotFoundException {

    private CreditCardNotFoundException(String message) {
        super(message);
    }

    public static CreditCardNotFoundException notFound() {
        throw new CreditCardNotFoundException("Cartão de crédito não encontrado");
    }

    public static void throwNotFound(String message) {
        throw new CreditCardNotFoundException(message);
    }

}
