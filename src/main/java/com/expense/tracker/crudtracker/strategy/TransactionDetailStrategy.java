package com.expense.tracker.crudtracker.strategy;

import com.expense.tracker.crudtracker.dto.TransactionDetailRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionResponseDto;
import com.expense.tracker.crudtracker.entity.Transaction;
import com.expense.tracker.crudtracker.entity.TransactionType;

public interface TransactionDetailStrategy {

    TransactionType supports();

    TransactionResponseDto registerTransactionDetail(Transaction transaction, TransactionDetailRequestDto detailRequestDto);

    TransactionResponseDto getTransactionDetail(Transaction transaction);

}
