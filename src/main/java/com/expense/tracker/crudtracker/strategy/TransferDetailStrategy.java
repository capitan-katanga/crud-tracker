package com.expense.tracker.crudtracker.strategy;

import com.expense.tracker.crudtracker.dto.TransactionDetailRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionResponseDto;
import com.expense.tracker.crudtracker.dto.transfer.TransferRequestDto;
import com.expense.tracker.crudtracker.entity.Transaction;
import com.expense.tracker.crudtracker.entity.TransactionType;
import com.expense.tracker.crudtracker.mapper.TransferDetailMapper;
import com.expense.tracker.crudtracker.repository.TransferDetailRepository;
import org.springframework.stereotype.Component;

@Component
public class TransferDetailStrategy implements TransactionDetailStrategy {

    private final TransferDetailRepository repository;
    private final TransferDetailMapper mapper;

    public TransferDetailStrategy(TransferDetailRepository repository, TransferDetailMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public TransactionType supports() {
        return TransactionType.TRANSFER;
    }

    @Override
    public TransactionResponseDto registerTransactionDetail(Transaction transaction, TransactionDetailRequestDto detailRequestDto) {
        if (!(detailRequestDto instanceof TransferRequestDto transferDetail)) {
            throw new UnsupportedOperationException("Transfer details are not supported");
        }
        var transferDetailEntity = mapper.toEntity(transaction, transferDetail);
        var savedTransferDetailEntity = repository.save(transferDetailEntity);
        return mapper.toResponseDto(savedTransferDetailEntity);
    }

    @Override
    public TransactionResponseDto getTransactionDetail(Transaction transaction) {
        var transferDetailEntity = repository.findByTransaction(transaction)
                .orElseThrow(() -> new IllegalArgumentException("Transfer details not found for transaction ID: " + transaction.getId()));
        return mapper.toResponseDto(transferDetailEntity);
    }

}
