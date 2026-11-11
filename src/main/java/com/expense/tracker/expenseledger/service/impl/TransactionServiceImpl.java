package com.expense.tracker.expenseledger.service.impl;

import com.expense.tracker.expenseledger.dto.TransactionRequestDto;
import com.expense.tracker.expenseledger.dto.TransactionResponseDto;
import com.expense.tracker.expenseledger.entity.TransactionType;
import com.expense.tracker.expenseledger.exception.TransactionNotFoundException;
import com.expense.tracker.expenseledger.exception.UnsupportedTransactionTypeException;
import com.expense.tracker.expenseledger.mapper.TransactionMapper;
import com.expense.tracker.expenseledger.repository.TransactionRepository;
import com.expense.tracker.expenseledger.service.TransactionService;
import com.expense.tracker.expenseledger.strategy.TransactionDetailStrategy;
import com.expense.tracker.expenseledger.strategy.TransactionDetailStrategyRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository repository;
    private final TransactionMapper mapper;
    private final TransactionDetailStrategyRegister strategyRegister;

    public TransactionServiceImpl(TransactionRepository repository, TransactionMapper mapper, TransactionDetailStrategyRegister strategyRegister) {
        this.repository = repository;
        this.mapper = mapper;
        this.strategyRegister = strategyRegister;
    }

    @Override
    public TransactionResponseDto registerTransaction(TransactionRequestDto requestDto) {
        log.info("Starting registration of transaction for request: {}", requestDto);
        var savedTransaction = repository.save(mapper.toEntity(requestDto));
        log.info("Saved transaction with ID: {}", savedTransaction.getId());
        if (savedTransaction.getType() == TransactionType.UNKNOWN || requestDto.detail() == null) {
            log.info("Transaction type is UNKNOWN or detail is null, skipping detail registration.");
            return mapper.toResponseDto(savedTransaction);
        }
        var strategy = getStrategyForType(savedTransaction.getType());
        return strategy.registerTransactionDetail(savedTransaction, requestDto.detail());
    }

    @Override
    public TransactionResponseDto getTransaction(UUID transactionId) {
        var transaction = repository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));
        log.info("Get transaction with ID: {}", transaction.getId());
        if (transaction.getType() == TransactionType.UNKNOWN) {
            log.info("Transaction type is UNKNOWN, skipping detail retrieval.");
            return mapper.toResponseDto(transaction);
        }
        var strategy = getStrategyForType(transaction.getType());
        return strategy.getTransactionDetail(transaction);
    }

    private TransactionDetailStrategy getStrategyForType(TransactionType type) {
        log.info("Getting strategy for type {}", type);
        var strategy = strategyRegister.getTransactionStrategy(type);
        if (strategy == null) {
            throw new UnsupportedTransactionTypeException("No strategy found for transaction type: " + type);
        }
        return strategy;
    }

}
