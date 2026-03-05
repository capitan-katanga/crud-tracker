package com.expense.tracker.crudtracker.dto;

import com.expense.tracker.crudtracker.dto.transfer.TransferResponseDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Transaction response payload. Contains main transaction fields and polymorphic detail.")
public record TransactionResponseDto(
        @Schema(description = "UUID of the transaction.", example = "c952509d-d65b-427e-9f3a-abddf3e0d568")
        UUID id,

        @Schema(description = "UUID of the user who owns the transaction.", example = "12c40443-acc4-440d-8091-05a1dc3b011a")
        UUID userId,

        @Schema(description = "Transaction type (TRANSFER, SERVICE_PAYMENT, etc).", example = "TRANSFER")
        String type,

        @Schema(description = "Amount of the transaction.", example = "1000.00")
        BigDecimal amount,

        @Schema(description = "Currency code (ISO 4217).", example = "ARS")
        String currency,

        @Schema(description = "Description of the transaction.", example = "Wire transfer to Carla Gonzalez")
        String description,

        @Schema(description = "Date and time the transaction happened.", example = "2024-06-01T12:44:00")
        LocalDateTime transactionDate,

        @Schema(description = "Timestamp when the transaction was registered.", example = "2024-06-01T12:48:17")
        LocalDateTime createdAt,

        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type"
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = TransferResponseDto.class, name = "TRANSFER")
                // add new type here.
        })
        @Schema(description = "Polymorphic detail, resolved by the transaction 'type'.")
        TransactionDetailResponseDto detail
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private UUID userId;
        private String type;
        private BigDecimal amount;
        private String currency;
        private String description;
        private LocalDateTime transactionDate;
        private LocalDateTime createdAt;
        private TransactionDetailResponseDto detail;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder transactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder detail(TransactionDetailResponseDto transferDetail) {
            this.detail = transferDetail;
            return this;
        }

        public TransactionResponseDto build() {
            return new TransactionResponseDto(this.id, this.userId, this.type, this.amount, this.currency,
                    this.description, this.transactionDate, this.createdAt, this.detail);
        }
    }
}
