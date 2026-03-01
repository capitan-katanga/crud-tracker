package com.expense.tracker.crudtracker.dto;

import com.expense.tracker.crudtracker.dto.transfer.TransferRequestDto;
import com.expense.tracker.crudtracker.entity.TransactionType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionRequestDto(@NotNull UUID userId,
                                    TransactionType type,
                                    @NotNull BigDecimal amount,
                                    String currency,
                                    String description,
                                    @NotNull LocalDateTime transactionDate,
                                    @Valid
                                    @JsonTypeInfo(
                                            use = JsonTypeInfo.Id.NAME,
                                            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                                            property = "type"
                                    )
                                    @JsonSubTypes({
                                            @JsonSubTypes.Type(value = TransferRequestDto.class, name = "TRANSFER")
                                            // add new type here.
                                    })
                                    TransactionDetailRequestDto detail) {

    public TransactionRequestDto {
        if (type == null) {
            type = TransactionType.UNKNOWN;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID userId;
        private TransactionType type;
        private BigDecimal amount;
        private String currency;
        private String description;
        private LocalDateTime transactionDate;
        private TransactionDetailRequestDto detail;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder type(TransactionType type) {
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

        public Builder detail(TransactionDetailRequestDto transferDetail) {
            this.detail = transferDetail;
            return this;
        }

        public TransactionRequestDto build() {
            return new TransactionRequestDto(this.userId, this.type, this.amount, this.currency,
                    this.description, this.transactionDate, this.detail);
        }
    }
}
