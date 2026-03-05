package com.expense.tracker.crudtracker.strategy;

import com.expense.tracker.crudtracker.entity.TransactionType;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TransactionDetailStrategyRegister {

    private final Map<TransactionType, TransactionDetailStrategy> strategyMap;

    public TransactionDetailStrategyRegister(List<TransactionDetailStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        TransactionDetailStrategy::supports,
                        Function.identity()
                ));
    }

    public @Nullable TransactionDetailStrategy getTransactionStrategy(TransactionType transactionType) {
        return strategyMap.get(transactionType);
    }

}
