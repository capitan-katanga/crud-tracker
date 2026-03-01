package com.expense.tracker.crudtracker.service.impl;

import com.expense.tracker.crudtracker.dto.transfer.TransferResponseDto;
import com.expense.tracker.crudtracker.entity.Transaction;
import com.expense.tracker.crudtracker.entity.TransactionType;
import com.expense.tracker.crudtracker.exception.TransactionNotFoundException;
import com.expense.tracker.crudtracker.mapper.TransactionMapper;
import com.expense.tracker.crudtracker.mock.TestUtils;
import com.expense.tracker.crudtracker.repository.TransactionRepository;
import com.expense.tracker.crudtracker.strategy.TransactionDetailStrategy;
import com.expense.tracker.crudtracker.strategy.TransactionDetailStrategyRegister;
import com.expense.tracker.crudtracker.strategy.TransferDetailStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;
    @Mock
    private TransactionDetailStrategyRegister strategyRegister;
    @Spy
    private TransactionMapper mapper;
    @InjectMocks
    private TransactionServiceImpl service;

    @ParameterizedTest(name = "Register transaction successfully - type: {0}")
    @EnumSource(TransactionType.class)
    @DisplayName("Register transaction successfully for all TransactionTypes without detail.")
    void registersTransactionSuccessfully(TransactionType type) {
        var transaction = TestUtils.createTransactionEntity(type);
        when(repository.save(any(Transaction.class)))
                .thenReturn(transaction);

        var result = service.registerTransaction(TestUtils.createTransactionRequestDto(type));

        Assertions.assertAll(
                () -> assertThat(result.id(), equalTo(transaction.getId())),
                () -> assertThat(result.userId(), equalTo(transaction.getUserId())),
                () -> assertThat(result.type(), equalTo(transaction.getType().name())),
                () -> assertThat(result.amount(), equalTo(transaction.getAmount())),
                () -> assertThat(result.currency(), equalTo(transaction.getCurrency())),
                () -> assertThat(result.description(), equalTo(transaction.getDescription())),
                () -> assertThat(result.transactionDate(), equalTo(transaction.getTransactionDate()))
        );

        verify(repository, times(1)).save(any(Transaction.class));
        verifyNoInteractions(strategyRegister);
    }

    @Mock
    private TransferDetailStrategy transferDetailStrategy;

    @Test
    @DisplayName("Register transaction TRANSFER with detail.")
    void registersTransferTransactionWithDetail() {
        var transaction = TestUtils.createTransactionEntity(TransactionType.TRANSFER);
        var request = TestUtils.createTransactionRequestDto(TransactionType.TRANSFER, TestUtils.createTransferRequestDto());
        var expectedResponse = TestUtils.createTransactionResponseDtoWithTransferDetail();

        when(repository.save(any(Transaction.class)))
                .thenReturn(transaction);
        when(strategyRegister.getTransactionStrategy(TransactionType.TRANSFER))
                .thenReturn(transferDetailStrategy);
        when(transferDetailStrategy.registerTransactionDetail(transaction, request.detail()))
                .thenReturn(expectedResponse);

        var result = service.registerTransaction(request);

        Assertions.assertAll(
                () -> assertThat(result.id(), equalTo(transaction.getId())),
                () -> assertThat(result.userId(), equalTo(transaction.getUserId())),
                () -> assertThat(result.type(), equalTo(TransactionType.TRANSFER.name())),
                () -> assertThat(result.amount(), equalTo(transaction.getAmount())),
                () -> assertThat(result.currency(), equalTo(transaction.getCurrency())),
                () -> assertThat(result.description(), equalTo(transaction.getDescription())),
                () -> assertThat(result.transactionDate(), equalTo(transaction.getTransactionDate())),
                () -> assertThat(result.detail(), equalTo(expectedResponse.detail()))
        );

        verify(repository, times(1)).save(any(Transaction.class));
        verify(strategyRegister, times(1)).getTransactionStrategy(TransactionType.TRANSFER);
        verify(transferDetailStrategy, times(1)).registerTransactionDetail(transaction, request.detail());
    }

    /// GET TRANSACTION TESTS ///

    @Test
    @DisplayName("Get UNKNOWN transaction type (without transaction detail).")
    void getUnknownTransactionType() {
        var transaction = TestUtils.createTransactionEntity(TransactionType.UNKNOWN);
        when(repository.findById(any(UUID.class)))
                .thenReturn(Optional.of(transaction));
        var result = service.getTransaction(transaction.getId());

        Assertions.assertAll(
                () -> assertThat(result.id(), equalTo(transaction.getId())),
                () -> assertThat(result.userId(), equalTo(transaction.getUserId())),
                () -> assertThat(result.type(), equalTo(transaction.getType().name())),
                () -> assertThat(result.amount(), equalTo(transaction.getAmount())),
                () -> assertThat(result.currency(), equalTo(transaction.getCurrency())),
                () -> assertThat(result.description(), equalTo(transaction.getDescription())),
                () -> assertThat(result.transactionDate(), equalTo(transaction.getTransactionDate())),
                () -> assertThat(result.detail(), nullValue())
        );

        verify(repository, times(1)).findById(any(UUID.class));
        verifyNoInteractions(strategyRegister);
    }

    @Mock
    private TransactionDetailStrategy mockStrategy;

    @ParameterizedTest(name = "Get transaction successfully - type: {0}")
    @EnumSource(value = TransactionType.class, names = {"UNKNOWN"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Get transaction successfully for TransactionTypes with not detail.")
    void getTransactionSuccessfullyWithoutDetail(TransactionType type) {
        var transaction = TestUtils.createTransactionEntity(type);
        when(repository.findById(any(UUID.class)))
                .thenReturn(Optional.of(transaction));
        when(strategyRegister.getTransactionStrategy(type))
                .thenReturn(mockStrategy);

        var expectedResponse = mapper.toResponseDto(transaction);
        when(mockStrategy.getTransactionDetail(transaction))
                .thenReturn(expectedResponse);

        var result = service.getTransaction(transaction.getId());

        Assertions.assertAll(
                () -> assertThat(result.id(), equalTo(transaction.getId())),
                () -> assertThat(result.userId(), equalTo(transaction.getUserId())),
                () -> assertThat(result.type(), equalTo(transaction.getType().name())),
                () -> assertThat(result.amount(), equalTo(transaction.getAmount())),
                () -> assertThat(result.currency(), equalTo(transaction.getCurrency())),
                () -> assertThat(result.description(), equalTo(transaction.getDescription())),
                () -> assertThat(result.transactionDate(), equalTo(transaction.getTransactionDate())),
                () -> assertThat(result.detail(), nullValue())
        );
        verify(repository, times(1)).findById(any(UUID.class));
        verify(strategyRegister, times(1)).getTransactionStrategy(type);
        verify(mockStrategy, times(1)).getTransactionDetail(transaction);
    }

    @Test
    @DisplayName("Get transaction successfully with TRANSFER detail.")
    void getTransactionSuccessfullyWithDetail() {
        var transaction = TestUtils.createTransactionEntity(TransactionType.TRANSFER);
        var transferDetail = TestUtils.createTransferResponseDto();
        when(repository.findById(any(UUID.class)))
                .thenReturn(Optional.of(transaction));

        when(strategyRegister.getTransactionStrategy(TransactionType.TRANSFER))
                .thenReturn(transferDetailStrategy);

        var expectedResponse = mapper.toResponseDto(transaction, transferDetail);

        when(transferDetailStrategy.getTransactionDetail(transaction))
                .thenReturn(expectedResponse);

        var result = service.getTransaction(transaction.getId());

        Assertions.assertAll(
                () -> assertThat(result.id(), equalTo(transaction.getId())),
                () -> assertThat(result.userId(), equalTo(transaction.getUserId())),
                () -> assertThat(result.type(), equalTo(transaction.getType().name())),
                () -> assertThat(result.amount(), equalTo(transaction.getAmount())),
                () -> assertThat(result.currency(), equalTo(transaction.getCurrency())),
                () -> assertThat(result.description(), equalTo(transaction.getDescription())),
                () -> assertThat(result.transactionDate(), equalTo(transaction.getTransactionDate())),
                () -> assertThat(result.detail(), instanceOf(TransferResponseDto.class)),
                () -> assertThat(result.detail(), equalTo(transferDetail)
                ));
        verify(repository, times(1)).findById(any(UUID.class));
        verify(strategyRegister, times(1)).getTransactionStrategy(TransactionType.TRANSFER);
        verify(transferDetailStrategy, times(1)).getTransactionDetail(transaction);
    }

    @Test
    @DisplayName("Get transaction - transaction not found.")
    void getTransactionNotFound() {
        var randomId = UUID.randomUUID();
        when(repository.findById(randomId))
                .thenReturn(Optional.empty());
        var response = Assertions.assertThrows(TransactionNotFoundException.class,
                () -> service.getTransaction(randomId));

        assertThat(response.getMessage(), equalTo("Transaction not found with ID: " + randomId));
    }

}
