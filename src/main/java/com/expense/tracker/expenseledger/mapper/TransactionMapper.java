package com.expense.tracker.expenseledger.mapper;

import com.expense.tracker.expenseledger.dto.TransactionDetailResponseDto;
import com.expense.tracker.expenseledger.dto.TransactionRequestDto;
import com.expense.tracker.expenseledger.dto.TransactionResponseDto;
import com.expense.tracker.expenseledger.entity.Transaction;
import com.expense.tracker.expenseledger.entity.TransactionType;


public class TransactionMapper {

    public Transaction toEntity(TransactionRequestDto requestDto) {
        return new Transaction(
                null,
                requestDto.userId(),
                TransactionType.safeResolveType(requestDto.type()),
                requestDto.amount(),
                requestDto.currency(),
                requestDto.description(),
                requestDto.transactionDate()
        );
    }

    public TransactionResponseDto toResponseDto(Transaction entity, TransactionDetailResponseDto responseDetailDto) {
        return toResponseDtoBuilder(entity, responseDetailDto).build();
    }

    public TransactionResponseDto toResponseDto(Transaction entity) {
        return toResponseDtoBuilder(entity, null).build();

    }

    private TransactionResponseDto.Builder toResponseDtoBuilder(Transaction entity, TransactionDetailResponseDto responseDetailDto) {
        return TransactionResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(String.valueOf(entity.getType()))
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .description(entity.getDescription())
                .transactionDate(entity.getTransactionDate())
                .createdAt(entity.getCreatedAt())
                .detail(responseDetailDto);
    }

}
