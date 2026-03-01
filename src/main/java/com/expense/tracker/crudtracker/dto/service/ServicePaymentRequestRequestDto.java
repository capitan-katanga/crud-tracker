package com.expense.tracker.crudtracker.dto.service;

import com.expense.tracker.crudtracker.dto.TransactionDetailRequestDto;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("SERVICE_PAYMENT")
public record ServicePaymentRequestRequestDto(String serviceName,
                                              String customerIdentifier,
                                              String paymentMethod,
                                              String paymentChannel,
                                              String paymentProcessor,
                                              String bankName,
                                              String transactionNumber,
                                              String controlNumber) implements TransactionDetailRequestDto {


}
