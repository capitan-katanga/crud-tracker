package com.expense.tracker.crudtracker.mock;

import com.expense.tracker.crudtracker.dto.TransactionDetailRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionRequestDto;
import com.expense.tracker.crudtracker.dto.TransactionResponseDto;
import com.expense.tracker.crudtracker.dto.transfer.TransferRequestDto;
import com.expense.tracker.crudtracker.dto.transfer.TransferResponseDto;
import com.expense.tracker.crudtracker.entity.Transaction;
import com.expense.tracker.crudtracker.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class TestUtils {

    public static final UUID TEST_TRANSACTION_ID = UUID.fromString("9f232847-8144-4902-b8f7-33635096b21b");
    public static final UUID TEST_USER_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    public static final BigDecimal TEST_AMOUNT = new BigDecimal("99.99");
    public static final LocalDateTime TEST_DATE = LocalDateTime.of(2024, 2, 24, 14, 1, 30);
    public static final String TEST_DESCRIPTION = "A test transaction";
    public static final String TEST_CURRENCY = "USD";
    public static final UUID TRANSFER_DETAIL_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    public static final String TEST_SENDER_NAME = "Juan Perez";
    public static final String TEST_SENDER_CUIT = "20-12345678-9";
    public static final String TEST_SENDER_INSTITUTION = "Banco Nacion";
    public static final String TEST_SENDER_ACCOUNT = "0000123456789";
    public static final String TEST_RECEIVER_NAME = "Maria Lopez";
    public static final String TEST_RECEIVER_CUIT = "27-98765432-1";
    public static final String TEST_RECEIVER_INSTITUTION = "Banco Galicia";
    public static final String TEST_RECEIVER_ACCOUNT = "0000987654321";
    public static final String TEST_OPERATION_NUMBER = "OP-20240224-001";
    public static final String TEST_REFERENCE_CODE = "REF-ABC123";
    public static final String TEST_MOTIVE = "Pago de servicios";


    private TestUtils() {
    }

    public static TransactionRequestDto createTransactionRequestDto(TransactionType type) {
        return createTransactionRequestDto(type, null);
    }

    public static TransactionRequestDto createTransactionRequestDto(TransactionType type, TransactionDetailRequestDto detail) {
        return TransactionRequestDto.builder()
                .userId(TEST_USER_ID)
                .type(type)
                .amount(TEST_AMOUNT)
                .currency(TEST_CURRENCY)
                .description(TEST_DESCRIPTION)
                .transactionDate(TEST_DATE)
                .detail(detail)
                .build();
    }

    public static TransferResponseDto createTransferResponseDto() {
        return TransferResponseDto.builder()
                .id(TRANSFER_DETAIL_ID)
                .senderName(TEST_SENDER_NAME)
                .senderCuit(TEST_SENDER_CUIT)
                .senderInstitution(TEST_SENDER_INSTITUTION)
                .senderAccount(TEST_SENDER_ACCOUNT)
                .receiverName(TEST_RECEIVER_NAME)
                .receiverCuit(TEST_RECEIVER_CUIT)
                .receiverInstitution(TEST_RECEIVER_INSTITUTION)
                .receiverAccount(TEST_RECEIVER_ACCOUNT)
                .operationNumber(TEST_OPERATION_NUMBER)
                .referenceCode(TEST_REFERENCE_CODE)
                .motive(TEST_MOTIVE)
                .build();
    }

    public static Transaction createTransactionEntity(TransactionType type) {
        return new Transaction(
                TEST_TRANSACTION_ID,
                TEST_USER_ID,
                type,
                TEST_AMOUNT,
                TEST_CURRENCY,
                TEST_DESCRIPTION,
                TEST_DATE
        );
    }

    public static TransferRequestDto createTransferRequestDto() {
        return TransferRequestDto.builder()
                .senderName(TEST_SENDER_NAME)
                .senderCuit(TEST_SENDER_CUIT)
                .senderInstitution(TEST_SENDER_INSTITUTION)
                .senderAccount(TEST_SENDER_ACCOUNT)
                .receiverName(TEST_RECEIVER_NAME)
                .receiverCuit(TEST_RECEIVER_CUIT)
                .receiverInstitution(TEST_RECEIVER_INSTITUTION)
                .receiverAccount(TEST_RECEIVER_ACCOUNT)
                .operationNumber(TEST_OPERATION_NUMBER)
                .referenceCode(TEST_REFERENCE_CODE)
                .motive(TEST_MOTIVE)
                .build();
    }

    public static TransactionResponseDto createTransactionResponseDtoWithTransferDetail() {
        return TransactionResponseDto.builder()
                .id(TEST_TRANSACTION_ID)
                .userId(TEST_USER_ID)
                .type(TransactionType.TRANSFER.name())
                .amount(TEST_AMOUNT)
                .currency(TEST_CURRENCY)
                .description(TEST_DESCRIPTION)
                .transactionDate(TEST_DATE)
                .detail(createTransferResponseDto())
                .build();
    }

}
