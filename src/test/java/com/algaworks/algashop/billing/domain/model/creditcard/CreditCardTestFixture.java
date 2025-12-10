package com.algaworks.algashop.billing.domain.model.creditcard;

import java.util.UUID;

public class CreditCardTestFixture {

    private UUID customerId = UUID.randomUUID();
    private String lastNumbers = "1234";
    private String brand = "Visa";
    private Integer expMonth = 12;
    private Integer expYear = 2025;
    private String gatewayCreditCardCode = "12345";

    private CreditCardTestFixture() {
    }

    public static CreditCardTestFixture aCreditCard() {
        return new CreditCardTestFixture();
    }

    public CreditCard build() {
        return CreditCard.brandNew(customerId, lastNumbers, brand, expMonth, expYear, gatewayCreditCardCode);
    }

    public CreditCardTestFixture customerId(UUID customerId) {
        this.customerId = customerId;
        return this;
    }

    public CreditCardTestFixture lastNumbers(String lastNumbers) {
        this.lastNumbers = lastNumbers;
        return this;
    }

    public CreditCardTestFixture brand(String brand) {
        this.brand = brand;
        return this;
    }

    public CreditCardTestFixture expMonth(Integer expMonth) {
        this.expMonth = expMonth;
        return this;
    }

    public CreditCardTestFixture expYear(Integer expYear) {
        this.expYear = expYear;
        return this;
    }

    public CreditCardTestFixture gatewayCreditCardCode(String gatewayCreditCardCode) {
        this.gatewayCreditCardCode = gatewayCreditCardCode;
        return this;
    }

}
