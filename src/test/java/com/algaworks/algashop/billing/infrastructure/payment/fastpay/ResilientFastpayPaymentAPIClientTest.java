package com.algaworks.algashop.billing.infrastructure.payment.fastpay;

import com.algaworks.algashop.billing.presentation.exception.BadGatewayException;
import com.algaworks.algashop.billing.presentation.exception.GatewayTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ResilientFastpayPaymentAPIClientTest {

    private FastpayPaymentAPIClient delegate;
    private ResilientFastpayPaymentAPIClient client;

    @BeforeEach
    void setUp() {
        delegate = Mockito.mock(FastpayPaymentAPIClient.class);
        FrameworkRetryCircuitBreakerFactory factory = new FrameworkRetryCircuitBreakerFactory();
        client = new ResilientFastpayPaymentAPIClient(factory, delegate);
    }

    @Test
    @DisplayName("Should throw GatewayTimeoutException when Fastpay API times out")
    void findById_WhenNetworkTimeout_ShouldThrowGatewayTimeout() {
        when(delegate.findById(anyString()))
                .thenThrow(new ResourceAccessException("timeout"));

        assertThatThrownBy(() -> client.findById("pay_123"))
                .isInstanceOf(GatewayTimeoutException.class)
                .hasMessageContaining("Fastpay API Timeout");
    }

    @Test
    @DisplayName("Should throw BadGatewayException when Fastpay API returns client error")
    void capture_WhenHttpClientError_ShouldThrowBadGateway() {
        when(delegate.capture(any(FastpayPaymentInput.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST,
                        "bad request",
                        HttpHeaders.EMPTY,
                        null,
                        null
                ));

        assertThatThrownBy(() -> client.capture(FastpayPaymentInput.builder().build()))
                .isInstanceOf(BadGatewayException.class)
                .hasMessageContaining("Fastpay API Bad Gateway");
    }
}
