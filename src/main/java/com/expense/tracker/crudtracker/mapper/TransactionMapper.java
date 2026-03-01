package com.expense.tracker.crudtracker.mapper;

import com.expense.tracker.crudtracker.dto.TransactionDetailResponseDto;
import com.expense.tracker.crudtracker.dto.TransactionRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionResponseDto;
import com.expense.tracker.crudtracker.entity.Transaction;

public class TransactionMapper {

    public Transaction toEntity(TransactionRequestDto requestDto) {
        return new Transaction(
                null,
                requestDto.userId(),
                requestDto.type(),
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
