package com.algaworks.algashop.billing.infrastructure.persistence.creditcard;

import com.algaworks.algashop.billing.application.creditcard.query.CreditCardOutput;
import com.algaworks.algashop.billing.application.creditcard.query.CreditCardQueryService;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.algaworks.algashop.billing.infrastructure.utility.CreditCardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditCardQueryServiceImpl implements CreditCardQueryService {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardMapper mapper;

    @Override
    public CreditCardOutput findOne(UUID customerId, UUID creditCardId) {
        return creditCardRepository.findByCustomerIdAndId(customerId, creditCardId)
                .map(mapper::toOutput)
                .orElseThrow(CreditCardNotFoundException::of);
    }

    @Override
    public List<CreditCardOutput> findByCustomer(UUID customerId) {
        return mapper.toListOutput(creditCardRepository.findAllByCustomerId(customerId));
    }

}
