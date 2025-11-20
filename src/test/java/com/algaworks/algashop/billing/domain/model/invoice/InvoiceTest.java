package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.exception.DomainException;
import com.algaworks.algashop.billing.domain.model.invoice.fixture.InvoiceTestFixture;
import com.algaworks.algashop.billing.domain.model.utility.GeneratorId;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod.CREDIT_CARD;
import static com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod.GATEWAY_BALANCE;
import static com.algaworks.algashop.billing.domain.model.utility.GeneratorId.generateTimeBasedUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class InvoiceTest {

    private final static String ASSIGN_CODE = "12091991";

    @Test
    void givenValidData_whenIssue_thenShouldCreateInvoiceCorrectly() {
        var orderId = GeneratorId.gererateTSID().toString();
        var customerId = generateTimeBasedUUID();
        var payer = InvoiceTestFixture.aPayer();
        var lineItems = Set.of(InvoiceTestFixture.aLineItem());

        var invoice = Invoice.issue(orderId, customerId, payer, lineItems);

        assertThat(invoice.getOrderId()).isEqualTo(orderId);
        assertThat(invoice.getCustomerId()).isEqualTo(customerId);
        assertThat(invoice.getPayer()).isEqualTo(payer);
        assertThat(invoice.getItems()).hasSize(1);
        assertThat(InvoiceStatus.UNPAID).isEqualTo(invoice.getStatus());
    }

    @Test
    void givenUnpaidInvoice_whenMarkAsPaid_thenShouldUpdateStatusAndPaidAt() {
        var invoice = InvoiceTestFixture.anInvoice().build();
        invoice.markAsPaid();
        assertThat(invoice.isPaid()).isTrue();
        assertThat(invoice.getPaidAt()).isNotNull();
    }

    @Test
    void givenIssuedInvoice_whenCancel_thenShouldSetStatusCanceledCanceledAtAndReason() {
        var invoice = InvoiceTestFixture.anInvoice().build();
        invoice.cancel("Cancelou porque nao conseguiu pagar!");
        assertThat(invoice.isCanceled()).isTrue();
        assertThat(invoice.getCanceledAt()).isNotNull();
    }

    @Test
    void givenInvoice_whenChangePaymentSettings_thenShouldUpdatePaymentMethodAndCardId() {
        var creditCardId = generateTimeBasedUUID();
        var invoice = InvoiceTestFixture.anInvoice()
                .paymentSettings(CREDIT_CARD, creditCardId)
                .build();

        invoice.changePaymentSettings(GATEWAY_BALANCE, creditCardId);

        assertThat(GATEWAY_BALANCE).isEqualTo(invoice.getPaymentSettings().getMethod());
        assertThat(creditCardId).isEqualTo(invoice.getPaymentSettings().getCreditCardId());
    }

    @Test
    void givenInvoice_whenAssignPaymentGatewayCode_thenShouldSetGatewayCodeCorrectly() {
        var creditCardId = generateTimeBasedUUID();
        var invoice = InvoiceTestFixture.anInvoice()
                .paymentSettings(CREDIT_CARD, creditCardId)
                .build();

        invoice.assignPaymentGatewayCode(ASSIGN_CODE);
        assertThat(CREDIT_CARD).isEqualTo(invoice.getPaymentSettings().getMethod());
        assertThat(creditCardId).isEqualTo(invoice.getPaymentSettings().getCreditCardId());
        assertThat(invoice.getPaymentSettings().getGatewayCode()).isEqualTo(ASSIGN_CODE);
    }

    @Test
    void givenEmptyItems_whenIssueInvoice_thenShouldThrowException(){
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Invoice.issue(
                        GeneratorId.gererateTSID().toString(),
                        generateTimeBasedUUID(),
                        InvoiceTestFixture.aPayer(),
                        Set.of()));
    }

    @Test
    void givenPaidInvoice_whenMarkAsPaid_thenShouldThrowException() {
        var creditCardId = generateTimeBasedUUID();
        var invoice = InvoiceTestFixture.anInvoice()
                .status(InvoiceStatus.PAID)
                .paymentSettings(CREDIT_CARD, creditCardId)
                .build();

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(invoice::markAsPaid);
    }

    @Test
    void givenCanceledInvoice_whenMarkAsPaid_thenShouldThrowException() {
        var invoice = InvoiceTestFixture.anInvoice()
                .status(InvoiceStatus.CANCELED)
                .paymentSettings(CREDIT_CARD, generateTimeBasedUUID())
                .build();

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> invoice.cancel("Cancelou porque nao conseguiu pagar!"));
    }

    @Test
    void givenPaidInvoice_whenChangePaymentSettings_thenShouldThrowException() {
        var creditCardId = generateTimeBasedUUID();
        var invoice = InvoiceTestFixture.anInvoice()
                .status(InvoiceStatus.PAID)
                .paymentSettings(CREDIT_CARD, creditCardId)
                .build();

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> invoice.changePaymentSettings(GATEWAY_BALANCE, creditCardId));
    }

    @Test
    void givenPaidInvoice_whenAssignPaymentGatewayCode_thenShouldThrowException() {
        var creditCardId = generateTimeBasedUUID();
        var invoice = InvoiceTestFixture.anInvoice()
                .status(InvoiceStatus.PAID)
                .paymentSettings(CREDIT_CARD, creditCardId)
                .build();

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> invoice.assignPaymentGatewayCode(ASSIGN_CODE));
    }

    @Test
    void givenPaidInvoice_whenAssignPaymentGatewayCodeWhithoutPaymentSetthings_thenShouldThrowException() {
        var invoice = InvoiceTestFixture.anInvoice()
                .status(InvoiceStatus.UNPAID)
                .build();

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> invoice.assignPaymentGatewayCode(ASSIGN_CODE));
    }

}