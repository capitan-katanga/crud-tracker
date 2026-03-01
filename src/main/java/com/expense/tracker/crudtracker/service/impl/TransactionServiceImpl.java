package com.expense.tracker.crudtracker.service.impl;

import com.expense.tracker.crudtracker.dto.TransactionRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionResponseDto;
import com.expense.tracker.crudtracker.entity.TransactionType;
import com.expense.tracker.crudtracker.exception.TransactionNotFoundException;
import com.expense.tracker.crudtracker.mapper.TransactionMapper;
import com.expense.tracker.crudtracker.repository.TransactionRepository;
import com.expense.tracker.crudtracker.service.TransactionService;
import com.expense.tracker.crudtracker.strategy.TransactionDetailStrategy;
import com.expense.tracker.crudtracker.strategy.TransactionDetailStrategyRegister;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

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
        var transaction = mapper.toEntity(requestDto);
        var savedTransaction = repository.save(transaction);
        if (requestDto.type() == TransactionType.UNKNOWN || requestDto.detail() == null) {
            return mapper.toResponseDto(savedTransaction);
        }
        var strategy = getStrategyForType(savedTransaction.getType());
        return strategy.registerTransactionDetail(savedTransaction, requestDto.detail());
    }

    @Override
    public TransactionResponseDto getTransaction(UUID transactionId) {
        var transaction = repository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));
        if (transaction.getType() == TransactionType.UNKNOWN) {
            return mapper.toResponseDto(transaction);
        }
        var strategy = getStrategyForType(transaction.getType());
        return strategy.getTransactionDetail(transaction);
    }

    private TransactionDetailStrategy getStrategyForType(TransactionType type) {
        return strategyRegister.getTransactionStrategy(type);
    }

}
