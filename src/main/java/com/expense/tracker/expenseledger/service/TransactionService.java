package com.expense.tracker.expenseledger.service;

import com.expense.tracker.expenseledger.dto.TransactionRequestDto;
import com.expense.tracker.expenseledger.dto.TransactionResponseDto;

import java.util.UUID;

public interface TransactionService {

    TransactionResponseDto registerTransaction(TransactionRequestDto requestDto);

    TransactionResponseDto getTransaction(UUID transactionId);

}
