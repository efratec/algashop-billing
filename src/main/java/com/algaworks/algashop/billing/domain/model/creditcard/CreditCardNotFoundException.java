package com.algaworks.algashop.billing.domain.model.creditcard;


import com.algaworks.algashop.billing.domain.model.exception.DomainEntityNotFoundException;

public class CreditCardNotFoundException extends DomainEntityNotFoundException {

    private CreditCardNotFoundException(String message) {
        super(message);
    }

    public static CreditCardNotFoundException notFound() {
        return new CreditCardNotFoundException("Cartão de crédito não encontrado");
    }

    public static CreditCardNotFoundException notFound(String message) {
        return new CreditCardNotFoundException(message);
    }

}
