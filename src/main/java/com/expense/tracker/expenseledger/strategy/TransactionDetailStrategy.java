package com.expense.tracker.expenseledger.strategy;

import com.expense.tracker.expenseledger.dto.TransactionDetailRequestDto;
import com.expense.tracker.expenseledger.dto.TransactionResponseDto;
import com.expense.tracker.expenseledger.entity.Transaction;
import com.expense.tracker.expenseledger.entity.TransactionType;

public interface TransactionDetailStrategy {

    TransactionType supports();

    TransactionResponseDto registerTransactionDetail(Transaction transaction, TransactionDetailRequestDto detailRequestDto);

    TransactionResponseDto getTransactionDetail(Transaction transaction);

}
