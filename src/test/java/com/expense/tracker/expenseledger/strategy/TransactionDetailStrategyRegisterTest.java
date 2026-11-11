package com.expense.tracker.expenseledger.strategy;

import com.expense.tracker.expenseledger.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionDetailStrategyRegisterTest {

    @Mock
    private TransactionDetailStrategy transferStrategy;

    private TransactionDetailStrategyRegister register;

    @BeforeEach
    void setUp() {
        when(transferStrategy.supports()).thenReturn(TransactionType.TRANSFER);
        register = new TransactionDetailStrategyRegister(List.of(transferStrategy));
    }

    @Test
    @DisplayName("Returns the strategy registered for the type.")
    void returnsRegisteredStrategy() {
        assertThat(register.getTransactionStrategy(TransactionType.TRANSFER), sameInstance(transferStrategy));
    }

    @Test
    @DisplayName("Returns null when no strategy is registered for the type.")
    void returnsNullForUnregisteredType() {
        assertThat(register.getTransactionStrategy(TransactionType.SERVICE_PAYMENT), nullValue());
    }

}
