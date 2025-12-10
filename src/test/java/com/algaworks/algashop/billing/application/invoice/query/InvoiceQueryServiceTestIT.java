package com.algaworks.algashop.billing.application.invoice.query;

import com.algaworks.algashop.billing.domain.model.invoice.InvoiceRepository;
import com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod;
import com.algaworks.algashop.billing.domain.model.invoice.fixture.InvoiceTestFixture;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class InvoiceQueryServiceTestIT {

    private final InvoiceQueryService invoiceQueryService;
    private final InvoiceRepository invoiceRepository;

    @Test
    public void shouldFindByOrderId() {
        var invoice = InvoiceTestFixture.anInvoice().build();
        invoice.changePaymentSettings(PaymentMethod.GATEWAY_BALANCE, null);
        invoiceRepository.saveAndFlush(invoice);
        InvoiceOutput invoiceOutput = invoiceQueryService.findByOrderId(invoice.getOrderId());
        assertThat(invoiceOutput.getId()).isEqualTo(invoice.getId());
    }

}