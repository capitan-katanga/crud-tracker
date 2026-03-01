package com.expense.tracker.crudtracker.dto.service;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("SERVICE_PAYMENT")
public record ServicePaymentResponseDto(UUID id,
                                        String serviceName,
                                        String customerIdentifier,
                                        String paymentMethod,
                                        String paymentChannel,
                                        String paymentProcessor,
                                        String bankName,
                                        String transactionNumber,
                                        String controlNumber) {

}
