package com.expense.tracker.expenseledger.strategy;

import com.expense.tracker.expenseledger.dto.TransactionResponseDto;
import com.expense.tracker.expenseledger.dto.service.ServicePaymentRequestRequestDto;
import com.expense.tracker.expenseledger.entity.Transaction;
import com.expense.tracker.expenseledger.entity.TransactionType;
import com.expense.tracker.expenseledger.entity.TransferDetail;
import com.expense.tracker.expenseledger.exception.TransactionNotFoundException;
import com.expense.tracker.expenseledger.exception.UnsupportedTransactionTypeException;
import com.expense.tracker.expenseledger.mapper.TransferDetailMapper;
import com.expense.tracker.expenseledger.mock.TestUtils;
import com.expense.tracker.expenseledger.repository.TransferDetailRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferDetailStrategyTest {

    @Mock
    private TransferDetailRepository repository;
    @Spy
    private TransferDetailMapper mapper;
    @InjectMocks
    private TransferDetailStrategy strategy;

    @Test
    @DisplayName("Supports TRANSFER type.")
    void supportsTransfer() {
        assertThat(strategy.supports(), equalTo(TransactionType.TRANSFER));
    }

    @Test
    @DisplayName("Register transfer detail successfully.")
    void registersTransferDetail() {
        var transaction = TestUtils.createTransactionEntity(TransactionType.TRANSFER);
        var entity = TestUtils.createTransferDetailEntity(transaction);
        when(repository.save(any(TransferDetail.class)))
                .thenReturn(entity);

        var result = strategy.registerTransactionDetail(transaction, TestUtils.createTransferRequestDto());

        assertTransferResponse(result, transaction);
        verify(repository, times(1)).save(any(TransferDetail.class));
    }

    @Test
    @DisplayName("Register detail with a non-transfer request fails.")
    void registerRejectsNonTransferDetail() {
        var transaction = TestUtils.createTransactionEntity(TransactionType.TRANSFER);
        var detail = new ServicePaymentRequestRequestDto("Edenor", "123", "DEBIT", "APP", "Link", "Banco Nacion", "TX-1", "CTRL-1");

        var exception = Assertions.assertThrows(UnsupportedTransactionTypeException.class,
                () -> strategy.registerTransactionDetail(transaction, detail));

        assertThat(exception.getMessage(), equalTo("Transaction detail request is not of type TransferRequestDto"));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Get transfer detail successfully.")
    void getsTransferDetail() {
        var transaction = TestUtils.createTransactionEntity(TransactionType.TRANSFER);
        when(repository.findByTransaction(transaction))
                .thenReturn(Optional.of(TestUtils.createTransferDetailEntity(transaction)));

        var result = strategy.getTransactionDetail(transaction);

        assertTransferResponse(result, transaction);
        verify(repository, times(1)).findByTransaction(transaction);
    }

    @Test
    @DisplayName("Get transfer detail - detail not found.")
    void getTransferDetailNotFound() {
        var transaction = TestUtils.createTransactionEntity(TransactionType.TRANSFER);
        when(repository.findByTransaction(transaction))
                .thenReturn(Optional.empty());

        var exception = Assertions.assertThrows(TransactionNotFoundException.class,
                () -> strategy.getTransactionDetail(transaction));

        assertThat(exception.getMessage(),
                equalTo("Transfer details not found for transaction ID: " + transaction.getId()));
    }

    private static void assertTransferResponse(TransactionResponseDto result, Transaction transaction) {
        Assertions.assertAll(
                () -> assertThat(result.id(), equalTo(transaction.getId())),
                () -> assertThat(result.userId(), equalTo(transaction.getUserId())),
                () -> assertThat(result.type(), equalTo(TransactionType.TRANSFER.name())),
                () -> assertThat(result.amount(), equalTo(transaction.getAmount())),
                () -> assertThat(result.currency(), equalTo(transaction.getCurrency())),
                () -> assertThat(result.description(), equalTo(transaction.getDescription())),
                () -> assertThat(result.transactionDate(), equalTo(transaction.getTransactionDate())),
                () -> assertThat(result.detail(), equalTo(TestUtils.createTransferResponseDto()))
        );
    }

}
