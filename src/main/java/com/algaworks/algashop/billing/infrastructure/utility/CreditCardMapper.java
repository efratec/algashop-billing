package com.algaworks.algashop.billing.infrastructure.utility;

import com.algaworks.algashop.billing.application.creditcard.query.CreditCardOutput;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCard;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {InvoiceMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CreditCardMapper {

    CreditCardOutput toOutput(CreditCard creditCard);
    List<CreditCardOutput> toListOutput(List<CreditCard> creditCards);

}
