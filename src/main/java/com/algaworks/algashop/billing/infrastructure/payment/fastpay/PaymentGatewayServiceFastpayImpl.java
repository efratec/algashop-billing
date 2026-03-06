package com.algaworks.algashop.billing.infrastructure.payment.fastpay;

import com.algaworks.algashop.billing.domain.model.creditcard.CreditCard;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.algaworks.algashop.billing.domain.model.invoice.payment.Payment;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import com.algaworks.algashop.billing.presentation.exception.BadGatewayException;
import com.algaworks.algashop.billing.presentation.exception.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "algashop.integrations.payment.provider", havingValue = "FASTPAY")
@RequiredArgsConstructor
public class PaymentGatewayServiceFastpayImpl implements PaymentGatewayService {

    private final FastpayPaymentAPIClient fastpayPaymentAPIClient;
    private final CreditCardRepository creditCardRepository;

    @Override
    public Payment capture(PaymentRequest request) {
        FastpayPaymentInput input = convertToInput(request);
        FastpayPaymentModel response = null;

        try {
            response = fastpayPaymentAPIClient.capture(input);
        } catch (ResourceAccessException e) {
            GatewayTimeoutException.throwGatewayTimeout("Fastpay API Timeout", e);
        } catch (HttpClientErrorException e) {
            BadGatewayException.throwBadGateway("Fastpay API Bad Gateway", e);
        }

        return convertToPayment(response);
    }

    @Override
    public Payment findByCode(String gatewayCode) {
        return null;
    }

    private FastpayPaymentInput convertToInput(PaymentRequest request) {
        var payer = request.getPayer();
        var address = payer.getAddress();

        var builder = FastpayPaymentInput.builder()
                .totalAmount(request.getAmount())
                .referenceCode(request.getInvoiceId().toString())
                .fullName(payer.getFullName())
                .document(payer.getDocument())
                .phone(payer.getPhone())
                .zipCode(address.getZipCode())
                .addressLine1(address.getStreet() + ", " + address.getNumber())
                .addressLine2(address.getComplement())
                .replyToUrl("http://example.com/webhook");

        switch (request.getMethod()) {
            case CREDIT_CARD -> {
                builder.method(FastpayPaymentMethod.CREDIT.name());
                CreditCard creditCard = creditCardRepository.findById(request.getCreditCardId())
                        .orElseThrow(CreditCardNotFoundException::notFound);
                builder.creditCardId(creditCard.getGatewayCode());
            }
            case GATEWAY_BALANCE -> builder.method(FastpayPaymentMethod.GATEWAY_BALANCE.name());
        }

        return builder.build();
    }

    private Payment convertToPayment(FastpayPaymentModel response) {
        var builder = Payment.builder()
                .gatewayCode(response.getId())
                .invoiceId(UUID.fromString(response.getReferenceCode()));

        FastpayPaymentMethod fastpayPaymentMethod;

        try {
            fastpayPaymentMethod = FastpayPaymentMethod.valueOf(response.getMethod());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown payment method: " + response.getMethod());
        }

        FastpayPaymentStatus fastpayPaymentStatus;
        try {
            fastpayPaymentStatus = FastpayPaymentStatus.valueOf(response.getStatus());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown payment status: " + response.getStatus());
        }

        builder.method(FastpayEnumConverter.convert(fastpayPaymentMethod));
        builder.status(FastpayEnumConverter.convert(fastpayPaymentStatus));

        return builder.build();
    }

}
