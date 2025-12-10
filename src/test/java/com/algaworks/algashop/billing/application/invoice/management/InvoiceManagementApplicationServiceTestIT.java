package com.algaworks.algashop.billing.application.invoice.management;

import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardTestFixture;
import com.algaworks.algashop.billing.domain.model.invoice.*;
import com.algaworks.algashop.billing.domain.model.invoice.fixture.InvoiceTestFixture;
import com.algaworks.algashop.billing.domain.model.invoice.payment.Payment;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class InvoiceManagementApplicationServiceTestIT {

    private final InvoiceManagementApplicationService applicationService;
    private final InvoiceRepository invoiceRepository;
    private final CreditCardRepository creditCardRepository;

    @MockitoSpyBean
    private InvoiceService invoiceService;

    @MockitoBean
    private PaymentGatewayService paymentGatewayService;

    @Test
    public void shouldGenerateInvoiceWithCreditCardAsPayment() {
        var customerId = UUID.randomUUID();
        var creditCard = CreditCardTestFixture.aCreditCard().customerId(customerId).build();
        creditCardRepository.saveAndFlush(creditCard);

        GenerateInvoiceInput input = GenerateInvoiceInputTestFixture.anInput().customerId(customerId).build();

        input.setPaymentSettings(
                PaymentSettingsInput.builder()
                        .creditCardId(creditCard.getId())
                        .method(PaymentMethod.CREDIT_CARD)
                        .build()
        );

        UUID invoiceId = applicationService.generate(input);

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.UNPAID);
        assertThat(invoice.getOrderId()).isEqualTo(input.getOrderId());

        verify(invoiceService).issue(any(), any(), any(), any());
    }

    @Test
    public void shouldGenerateInvoiceWithGatewayBalanceAsPayment() {
        var customerId = UUID.randomUUID();
        GenerateInvoiceInput input = GenerateInvoiceInputTestFixture.anInput().customerId(customerId).build();

        input.setPaymentSettings(
                PaymentSettingsInput.builder()
                        .method(PaymentMethod.GATEWAY_BALANCE)
                        .build()
        );

        UUID invoiceId = applicationService.generate(input);

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.UNPAID);
        assertThat(invoice.getOrderId()).isEqualTo(input.getOrderId());

        verify(invoiceService).issue(any(), any(), any(), any());
    }

    @Test
    public void shouldProcessInvoicePayment() {
        Invoice invoice = InvoiceTestFixture.anInvoice().build();
        invoice.changePaymentSettings(PaymentMethod.GATEWAY_BALANCE, null);
        invoiceRepository.saveAndFlush(invoice);

        var payment = Payment.builder()
                .gatewayCode("12345")
                .invoiceId(invoice.getId())
                .method(invoice.getPaymentSettings().getMethod())
                .status(PaymentStatus.PAID)
                .build();

        Mockito.when(paymentGatewayService.capture(Mockito.any(PaymentRequest.class))).thenReturn(payment);

        applicationService.processPayment(invoice.getId());

        Invoice paidInvoice = invoiceRepository.findById(invoice.getId()).orElseThrow();

        assertThat(paidInvoice.isPaid()).isTrue();

        Mockito.verify(paymentGatewayService).capture(Mockito.any(PaymentRequest.class));
        Mockito.verify(invoiceService).assignPayment(Mockito.any(Invoice.class), Mockito.any(Payment.class));
    }

}