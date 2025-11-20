package com.algaworks.algashop.billing.domain.model.invoice;

import lombok.*;

import java.util.UUID;

import static com.algaworks.algashop.billing.domain.model.utility.GeneratorId.generateTimeBasedUUID;


@Setter(AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentSettings {

    @EqualsAndHashCode.Include
    private UUID id;
    private UUID creditCardId;
    private String gatewayCode;
    private PaymentMethod method;

    public static PaymentSettings brandNew(PaymentMethod method, UUID creditCardId) {
        return new PaymentSettings(
                generateTimeBasedUUID(),
                creditCardId,
                null,
                method
        );
    }

    void assignGatewayCode(String gatewayCode) {
        setGatewayCode(gatewayCode);
    }

}
