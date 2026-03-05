package com.expense.tracker.crudtracker.strategy;

import com.expense.tracker.crudtracker.dto.TransactionDetailRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionResponseDto;
import com.expense.tracker.crudtracker.dto.transfer.TransferRequestDto;
import com.expense.tracker.crudtracker.entity.Transaction;
import com.expense.tracker.crudtracker.entity.TransactionType;
import com.expense.tracker.crudtracker.exception.TransactionNotFoundException;
import com.expense.tracker.crudtracker.exception.UnsupportedTransactionTypeException;
import com.expense.tracker.crudtracker.mapper.TransferDetailMapper;
import com.expense.tracker.crudtracker.repository.TransferDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TransferDetailStrategy implements TransactionDetailStrategy {

    private static final Logger log = LoggerFactory.getLogger(TransferDetailStrategy.class);

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
        log.info("Starting registration of transfer details for transaction ID: {}", transaction.getId());
        if (!(detailRequestDto instanceof TransferRequestDto transferDetail)) {
            throw new UnsupportedTransactionTypeException("Transaction detail request is not of type TransferRequestDto");
        }
        var transferDetailEntity = mapper.toEntity(transaction, transferDetail);
        var savedTransferDetailEntity = repository.save(transferDetailEntity);
        log.info("Successfully registered transfer detail with ID: {}", savedTransferDetailEntity.getId());
        return mapper.toResponseDto(savedTransferDetailEntity);
    }

    @Override
    public TransactionResponseDto getTransactionDetail(Transaction transaction) {
        var transferDetailEntity = repository.findByTransaction(transaction)
                .orElseThrow(() -> new TransactionNotFoundException("Transfer details not found for transaction ID: " + transaction.getId()));
        log.info("Successfully found transfer detail with ID: {}", transferDetailEntity.getId());
        return mapper.toResponseDto(transferDetailEntity);
    }

}
