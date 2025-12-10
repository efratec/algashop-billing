package com.algaworks.algashop.billing.domain.model.invoice;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import static com.algaworks.algashop.billing.domain.model.utility.GeneratorId.generateTimeBasedUUID;


@Setter(AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PaymentSettings {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID creditCardId;
    private String gatewayCode;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @OneToOne(mappedBy = "paymentSettings")
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PACKAGE)
    private Invoice invoice;

    public static PaymentSettings brandNew(PaymentMethod method, UUID creditCardId) {
        return new PaymentSettings(
                generateTimeBasedUUID(),
                creditCardId,
                null,
                method,
                null
        );
    }

    void assignGatewayCode(String gatewayCode) {
        setGatewayCode(gatewayCode);
    }

}
