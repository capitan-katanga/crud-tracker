package com.expense.tracker.crudtracker.service;

import com.expense.tracker.crudtracker.dto.TransactionRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionResponseDto;

import java.util.UUID;

public interface TransactionService {

    TransactionResponseDto registerTransaction(TransactionRequestDto requestDto);

    TransactionResponseDto getTransaction(UUID transactionId);

}
