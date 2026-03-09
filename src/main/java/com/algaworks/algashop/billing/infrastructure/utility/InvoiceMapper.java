package com.algaworks.algashop.billing.infrastructure.utility;

import com.algaworks.algashop.billing.application.invoice.query.InvoiceOutput;
import com.algaworks.algashop.billing.domain.model.invoice.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    InvoiceOutput  toInvoiceOutput(Invoice invoice);

}
