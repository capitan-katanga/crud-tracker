package com.expense.tracker.crudtracker.dto;

import com.expense.tracker.crudtracker.dto.transfer.TransferResponseDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDto(UUID id,
                                     UUID userId,
                                     String type,
                                     BigDecimal amount,
                                     String currency,
                                     String description,
                                     LocalDateTime transactionDate,
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
                                     TransactionDetailResponseDto detail) {

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
